package com.luv2code.jobportal.controller;


import com.luv2code.jobportal.entity.*;
import com.luv2code.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing job applications and interactions
 * related to jobSeekers and recruiters.
 *
 * @author : Jim Wu
 */

@Controller
public class JobSeekerApplyController {

    // Service for managing job post activities (e.g., creating or retrieving job posts)
    private final JobPostActivityService jobPostActivityService;

    // Service for managing user-related operations (e.g., authentication, profiles)
    private final UsersService usersService;

    // Service for managing job applications submitted by jobSeekers
    private final JobSeekerApplyService jobSeekerApplyService;

    // Service for managing saved jobs by jobSeekers
    private final JobSeekerSaveService jobSeekerSaveService;

    // Service for managing recruiter profiles and their related activities
    private final RecruiterProfileService recruiterProfileService;

    // Service for managing jobSeeker profiles and their related activities
    private final JobSeekerProfileService jobSeekerProfileService;

    /**
     * Constructor for initializing services using dependency injection.
     *
     * @param jobPostActivityService : Service for handling job post activities.
     * @param usersService : Service for handling user-related operations.
     * @param jobSeekerApplyService : Service for managing job applications.
     * @param jobSeekerSaveService : Service for managing saved jobs.
     * @param recruiterProfileService : Service for handling recruiter profiles.
     * @param jobSeekerProfileService : Service for handling job seeker profiles.
     */
    @Autowired
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService,
                                    UsersService usersService,
                                    JobSeekerApplyService jobSeekerApplyService,
                                    JobSeekerSaveService jobSeekerSaveService,
                                    RecruiterProfileService recruiterProfileService,
                                    JobSeekerProfileService jobSeekerProfileService) {
        // Initialize services
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    /**
     * Handles GET requests for job details and application form.
     *
     * @param id : PostActivity ID - The unique identifier of the job post.
     * @return : The "job-details" form view.
     */
    @GetMapping("/job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        // Retrieves the job post details based on the given ID.
        JobPostActivity jobDetails = this.jobPostActivityService.getOne(id);

        // Fetches the list of job candidates who have applied or saved this job.
        List<JobSeekerApply> jobSeekerApplyList = this.jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = this.jobSeekerSaveService.getJobCandidates(jobDetails);

        // Retrieves authentication details to check if the user is logged in.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            // If the user is a recruiter, retrieve and display the list of applicants.
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile user = this.recruiterProfileService.getCurrentRecruiterProfile();
                if (user != null) {
                    model.addAttribute("applyList", jobSeekerApplyList);
                }
            } else {
                // If the user is a jobSeeker, check application and save status for the job.
                JobSeekerProfile user = this.jobSeekerProfileService.getCurrentSeekerProfile();
                if (user != null) {
                    boolean exist = false; // Flag to check if the job is already applied.
                    boolean saved = false; // Flag to check if the job is already saved.

                    // Iterate over the list of applicants to check if the current user has applied.
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            exist = true;
                            break;
                        }
                    }

                    // Iterate over the list of saved jobs to check if the current user has saved this job.
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            saved = true;
                            break;
                        }
                    }

                    // Add flags to the model indicating the application and save status.
                    model.addAttribute("alreadyApplied", exist);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }

        // Create a new JobSeekerApply object and add it to the model for form binding.
        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob", jobSeekerApply);

        // Add job post details and user profile to the model for rendering in the front-end.
        model.addAttribute("jobDetails", jobDetails);
        model.addAttribute("user", this.usersService.getCurrentUserProfile());

        // Return the view name for the job details page.
        return "job-details";
    }

    /**
     * Handles POST requests for job applications.
     *
     * @param id : The ID of the JobPostActivity (job post) the user wants to apply for.
     * @param jobSeekerApply : A JobSeekerApply object used to bind application details.
     * @return : Redirects the user back to the dashboard after applying.
     */
    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply) {

        // Retrieve the authentication details of the currently logged-in user.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) { // Check if the user is authenticated.
            // Retrieve the username (email) of the current user.
            String currentUserName = authentication.getName();

            // Find the user in the database using their email.
            Users user = this.usersService.findByEmail(currentUserName);

            // Retrieve the jobSeeker profile of the current user.
            Optional<JobSeekerProfile> seekerProfile = this.jobSeekerProfileService.getOne(user.getUserId());

            // Retrieve the job post details by its ID.
            JobPostActivity jobPostActivity = this.jobPostActivityService.getOne(id);

            // Check if the jobSeeker profile exists and the job post is valid.
            if (seekerProfile.isPresent() && jobPostActivity != null) {

                // Create a new JobSeekerApply object to store application details.
                jobSeekerApply = new JobSeekerApply();

                // Associate the jobSeeker profile with the application.
                jobSeekerApply.setUserId(seekerProfile.get());

                // Associate the job post with the application.
                jobSeekerApply.setJob(jobPostActivity);

                // Set the current date as the application date.
                jobSeekerApply.setApplyDate(new Date());
            } else {

                // Throw an exception if the user or job post is not found.
                throw new RuntimeException("User not found");
            }

            // Save the job application using the service.
            this.jobSeekerApplyService.addNew(jobSeekerApply);
        }

        // Redirect the user back to the dashboard page after successfully applying.
        return "redirect:/dashboard/";
    }

}
