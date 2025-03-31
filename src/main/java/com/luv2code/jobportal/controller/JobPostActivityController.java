package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.*;
import com.luv2code.jobportal.services.JobPostActivityService;
import com.luv2code.jobportal.services.JobSeekerApplyService;
import com.luv2code.jobportal.services.JobSeekerSaveService;
import com.luv2code.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
    Dashboard Controller
 */
@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.jobSeekerSaveService = jobSeekerSaveService;
        System.out.println("JobPostActivityController start up.......");
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
    }

    // The dashboard job and custom search
    @GetMapping("/dashboard/")
    public String searchJobs(Model model,
                             @RequestParam(value = "job", required = false) String job,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "partTime", required = false) String partTime,
                             @RequestParam(value = "fullTime", required = false) String fullTime,
                             @RequestParam(value = "freelance", required = false) String freelance,
                             @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly", required = false) String officeOnly,
                             @RequestParam(value = "partialRemote", required = false) String partialRemote,
                             @RequestParam(value = "today", required = false) boolean today,
                             @RequestParam(value = "days7", required = false) boolean days7,
                             @RequestParam(value = "days30", required = false) boolean days30

    ) {
        // Set attributes for front-end checkboxes based on request parameters
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);
        model.addAttribute("job", job);
        model.addAttribute("location", location);

        // Initialize variables for search filters and results
        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        // Determine the search date based on checkboxes
        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false; // No date filter
        }

        // Set default job types if none are selected
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false; // No specific remote filter
        }

        // Set default workplace types if none are selected
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false; // No specific workplace filter
        }

        // Retrieve all jobs if no filters are applied
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = this.jobPostActivityService.getAll();
        } else {
            // Retrieve jobs based on user-defined filters
            jobPost = this.jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }

        // Retrieve current user profile and authentication details
        Object currentUserProfile = this.usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If the user is authenticated, set additional attributes
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);

            // Check if the user is a recruiter and retrieve corresponding job data
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                List<RecruiterJobsDto> recruiterJobs = this.jobPostActivityService.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);
            } else {
                // If the user is a jobSeeker, retrieve applied and saved jobs

                // Get the jobSeekerApply and Save job list
                List<JobSeekerApply> jobSeekerApplyList = this.jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaveList = this.jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

                boolean exist;
                boolean saved;

                // Check the status of each job in the search results
                for (JobPostActivity jobActivity : jobPost) {
                    exist = false;
                    saved = false;

                    // Mark job as active if the user has applied for it
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                            jobActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }

                    // Mark job as saved if the user has saved it
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                            jobActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }

                    // Set default values if the job is neither active nor saved
                    if (!exist) {
                        jobActivity.setIsActive(false);
                    }
                    if (!saved) {
                        jobActivity.setIsSaved(false);
                    }


                }
                model.addAttribute("jobPost", jobPost);
            }
        }

        // Add user profile to the model for front-end rendering
        model.addAttribute("user", currentUserProfile);

        // Return the dashboard view
        return "dashboard";
    }

    @GetMapping("global-search/")
    public String globalSearch(Model model,
                               @RequestParam(value = "job", required = false) String job,
                               @RequestParam(value = "location", required = false) String location,
                               @RequestParam(value = "partTime", required = false) String partTime,
                               @RequestParam(value = "fullTime", required = false) String fullTime,
                               @RequestParam(value = "freelance", required = false) String freelance,
                               @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
                               @RequestParam(value = "officeOnly", required = false) String officeOnly,
                               @RequestParam(value = "partialRemote", required = false) String partialRemote,
                               @RequestParam(value = "today", required = false) boolean today,
                               @RequestParam(value = "days7", required = false) boolean days7,
                               @RequestParam(value = "days30", required = false) boolean days30) {

        // Set attributes for front-end checkboxes based on request parameters
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);
        model.addAttribute("job", job);
        model.addAttribute("location", location);

        // Initialize variables for search date, job list, and filter flags
        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        // Set the search date based on the selected checkbox
        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false; // No date filter applied
        }

        // If no job type is selected, set defaults and disable the remote filter
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false; // No specific job type filter
        }

        // If no workplace type is selected, set defaults and disable the type filter
        if (officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false; // No specific workplace filter
        }

        // Retrieve all jobs if no filters are applied
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = this.jobPostActivityService.getAll();
        } else {
            // Retrieve jobs based on user-defined search criteria
            jobPost = this.jobPostActivityService.search(
                    job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, partialRemote), searchDate);
        }

        // Add the list of job posts to the model for rendering in the front-end
        model.addAttribute("jobPost", jobPost);

        // Return the view name for the "global-search" page
        return "global-search";
    }

    // Handles GET requests for the "/dashboard/add" endpoint
    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {

        // Adds a new empty JobPostActivity object to the model for the front-end to use
        model.addAttribute("jobPostActivity", new JobPostActivity());

        // Retrieves the current user profile and adds it to the model
        model.addAttribute("user", this.usersService.getCurrentUserProfile());

        // Returns the view name "add-jobs", which corresponds to the form for adding jobs
        return "add-jobs";
    }

    // Handles POST requests for the "/dashboard/addNew" endpoint
    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {

        // Retrieves the current logged-in user
        Users user = this.usersService.getCurrentUser();

        // If the user exists, associate the job posting with the user's ID
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }

        // Sets the current date as the posted date for the job activity
        jobPostActivity.setPostedDate(new Date());

        // Adds the jobPostActivity to the model to pass data to the front-end
        model.addAttribute("jobPostActivity", jobPostActivity);

        // Saves the new job posting using the service and adds it to the database
        JobPostActivity saved = this.jobPostActivityService.addNew(jobPostActivity);

        // Redirects to the "/dashboard/" page after saving the job posting
        return "redirect:/dashboard/";
    }

    // Handles POST requests for the "dashboard/edit/{id}" endpoint
    @PostMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model) {

        // Retrieves the job posting details based on the given ID
        JobPostActivity jobPostActivity = this.jobPostActivityService.getOne(id);

        // Adds the retrieved jobPostActivity to the model for the front-end to edit
        model.addAttribute("jobPostActivity", jobPostActivity);

        // Retrieves the current user profile and adds it to the model
        model.addAttribute("user", this.usersService.getCurrentUserProfile());

        // Returns the view name "add-jobs", which displays the form for editing jobs
        return "add-jobs";
    }
}
