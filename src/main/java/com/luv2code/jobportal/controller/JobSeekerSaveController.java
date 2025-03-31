package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.JobPostActivity;
import com.luv2code.jobportal.entity.JobSeekerProfile;
import com.luv2code.jobportal.entity.JobSeekerSave;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.services.JobPostActivityService;
import com.luv2code.jobportal.services.JobSeekerProfileService;
import com.luv2code.jobportal.services.JobSeekerSaveService;
import com.luv2code.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing saved jobs functionality for jobSeekers.
 * Allows saving job posts and viewing saved jobs.
 *
 * @author : Jim Wu
 */
@Controller
public class JobSeekerSaveController {

    // Service for managing user-related operations
    private final UsersService usersService;

    // Service for managing jobSeeker profiles
    private final JobSeekerProfileService jobSeekerProfileService;

    // Service for managing job post activities
    private final JobPostActivityService jobPostActivityService;

    // Service for managing saved jobs by jobSeekers
    private final JobSeekerSaveService jobSeekerSaveService;

    /**
     * Constructor to initialize the required services via dependency injection.
     *
     * @param usersService : Service for user operations.
     * @param jobSeekerProfileService : Service for jobSeeker profiles.
     * @param jobPostActivityService : Service for job posts.
     * @param jobSeekerSaveService : Service for saved jobs functionality.
     */
    @Autowired
    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService,
                                   JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    /**
     * Handles POST requests to save a job post for the current jobSeeker.
     *
     * @param id : The ID of the job post to save.
     * @param jobSeekerSave : The JobSeekerSave object used to bind save details.
     * @return : Redirects to the dashboard after saving the job.
     */
    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {
        // Retrieve authentication details of the logged-in user.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated.
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Retrieve the username (email) of the current user.
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);

            // Fetch the jobSeeker profile and job post details.
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            // If valid profile and job post are found, create a new saved job entry.
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerSave = new JobSeekerSave();
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());
            } else {
                // Throw an exception if either the profile or job post is not found.
                throw new RuntimeException("User not found");
            }

            // Save the job post as a saved entry using the service.
            jobSeekerSaveService.addNew(jobSeekerSave);
        }

        // Redirect to the dashboard after saving the job.
        return "redirect:/dashboard/";
    }

    /**
     * Handles GET requests to retrieve a list of saved jobs for the current job seeker.
     *
     * @param model : The Model object for passing data to the view.
     * @return : Returns the "saved-jobs" view.
     */
    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {
        // Initialize a list to hold job posts and retrieve the current user profile.
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        // Fetch all saved jobs for the current jobSeeker and add them to the job list.
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }

        // Add job list and user profile to the model for rendering in the view.
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        // Return the view for displaying saved jobs.
        return "saved-jobs";
    }
}
