package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.JobSeekerProfile;
import com.luv2code.jobportal.entity.Skills;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.repository.UsersRepository;
import com.luv2code.jobportal.services.JobSeekerProfileService;
import com.luv2code.jobportal.util.FileDownloadUtil;
import com.luv2code.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for managing job seeker profiles, including profile creation, update, and file handling.
 *
 * @author : Jim Wu
 */
@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    // Service for managing jobSeeker profiles
    private JobSeekerProfileService jobSeekerProfileService;

    // Repository for accessing user data
    private UsersRepository usersRepository;

    /**
     * Constructor for initializing the controller with required services.
     *
     * @param jobSeekerProfileService : Service for managing jobSeeker profiles.
     * @param usersRepository : Repository for accessing user-related data.
     */
    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    /**
     * Handles GET requests to display the jobSeeker profile page.
     *
     * @param model : Model object for passing data to the view.
     * @return : Returns the "job-seeker-profile" view.
     */
    @GetMapping("/")
    public String jobSeekerProfile(Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skills = new ArrayList<>();

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Retrieve the user by email and fetch their job seeker profile
            Users user = this.usersRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Optional<JobSeekerProfile> seekerProfile = this.jobSeekerProfileService.getOne(user.getUserId());
            if (seekerProfile.isPresent()) {
                jobSeekerProfile = seekerProfile.get();
                // Add a default skill if no skills exist
                if (jobSeekerProfile.getSkills().isEmpty()) {
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }

            // Add the profile and skills to the model for the view
            model.addAttribute("skills", skills);
            model.addAttribute("profile", jobSeekerProfile);
        }

        return "job-seeker-profile";
    }

    /**
     * Handles POST requests to create or update a jobSeeker profile.
     *
     * @param jobSeekerProfile : JobSeeker profile data submitted from the form.
     * @param image : Uploaded profile photo.
     * @param pdf : Uploaded resume file.
     * @param model : Model object for passing data to the view.
     * @return : Redirects the user to the dashboard upon success.
     */
    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, @RequestParam("image") MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Retrieve the user by email and associate their ID with the job seeker profile
            Users user = this.usersRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }

        // Initialize skills and add them to the model for the view
        List<Skills> skillsList = new ArrayList<>();
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);

        // Link the skills to the jobSeeker profile
        for (Skills skills : jobSeekerProfile.getSkills()) {
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        // Process uploaded profile photo and resume
        String imageName = "";
        String resumeName = "";

        if (!Objects.equals(image.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if (!Objects.equals(pdf.getOriginalFilename(), "")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        // Save the jobSeeker profile
        JobSeekerProfile seekerProfile = this.jobSeekerProfileService.addNew(jobSeekerProfile);

        // Save uploaded files to the server
        try {
            String uploadDir = "photos/candidate/" + jobSeekerProfile.getUserAccountId();
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, image);
            }
            if (!Objects.equals(pdf.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return "redirect:/dashboard/";
    }

    /**
     * Handles GET requests to display a candidate's profile by ID.
     *
     * @param id : The unique ID of the jobSeeker profile.
     * @param model : Model object for passing data to the view.
     * @return : Returns the "job-seeker-profile" view.
     */
    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model) {
        // Fetch the jobSeeker profile by ID and add it to the model
        Optional<JobSeekerProfile> seekerProfile = this.jobSeekerProfileService.getOne(id);
        model.addAttribute("profile", seekerProfile.get());
        return "job-seeker-profile";
    }

    /**
     * Handles GET requests to download a candidate's resume.
     *
     * @param fileName : Name of the resume file.
     * @param userId : ID of the candidate whose resume is being downloaded.
     * @return : A ResponseEntity containing the file resource or an error response.
     */
    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String fileName,
                                            @RequestParam(value = "userID") String userId) {
        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();
        Resource resource = null;

        try {
            // Retrieve the file resource from the directory
            resource = fileDownloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
        } catch (IOException io) {
            return ResponseEntity.badRequest().build();
        }

        // If file not found, return an error response
        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        // Prepare the response for file download
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}