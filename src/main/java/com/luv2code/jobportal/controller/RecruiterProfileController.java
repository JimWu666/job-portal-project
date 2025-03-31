package com.luv2code.jobportal.controller;


import com.luv2code.jobportal.entity.RecruiterProfile;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.repository.UsersRepository;
import com.luv2code.jobportal.services.RecruiterProfileService;
import com.luv2code.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller for managing recruiter profiles.
 * Provides functionality for retrieving and updating recruiter profiles.
 * @author : Jim Wu
 */
@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    // Repository for accessing user data
    private final UsersRepository usersRepository;

    // Service for managing recruiter profiles
    private final RecruiterProfileService recruiterProfileService;

    /**
     * Constructor for initializing the controller with required dependencies.
     *
     * @param usersRepository : Repository for accessing user-related data.
     * @param recruiterProfileService : Service for managing recruiter profiles.
     */
    @Autowired
    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    /**
     * Handles GET requests to retrieve the current recruiter's profile.
     *
     * @param model : Model object for passing data to the view.
     * @return : Returns the "recruiter_profile" view.
     */
    @GetMapping("/")
    public String recruiterProfile(Model model) {
        // Retrieve the authentication details of the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Fetch the current user's email
            String currentUsername = authentication.getName();

            // Retrieve the user by email
            Users users = this.usersRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));

            // Retrieve the recruiter profile for the user's ID
            Optional<RecruiterProfile> recruiterProfile = this.recruiterProfileService.getOne(users.getUserId());

            // If a recruiter profile exists, add it to the model for rendering in the view
            recruiterProfile.ifPresent(profile -> model.addAttribute("profile", profile));
        }

        return "recruiter_profile";
    }

    /**
     * Handles POST requests to add or update a recruiter's profile.
     *
     * @param recruiterProfile : Recruiter profile data submitted from the form.
     * @param multipartFile : Uploaded profile photo file.
     * @param model : Model object for passing data to the view.
     * @return : Redirects the user to the dashboard upon successful save.
     */
    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile, Model model) {
        // Debugging information (prints first and last name)
        System.out.println("============================" + recruiterProfile.getFirstName());
        System.out.println("============================" + recruiterProfile.getLastName());

        // Retrieve the authentication details of the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Fetch the current user's email
            String currentUsername = authentication.getName();

            // Retrieve the user by email and associate them with the recruiter profile
            Users users = this.usersRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }

        // Add the profile data to the model for rendering in the view
        model.addAttribute("profile", recruiterProfile);

        // Process the uploaded profile photo
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName); // Set the file name in the recruiter profile
        }

        // Save the recruiter profile using the service
        RecruiterProfile savedUser = this.recruiterProfileService.addNew(recruiterProfile);

        // Save the profile photo to the specified directory
        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } catch (Exception ex) {
            ex.printStackTrace(); // Log any exception that occurs during file upload
        }

        return "redirect:/dashboard/"; // Redirect to the dashboard after saving
    }
}
