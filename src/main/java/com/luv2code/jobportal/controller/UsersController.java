package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.entity.UsersType;
import com.luv2code.jobportal.services.UsersService;
import com.luv2code.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing user-related actions, including registration, login, and logout.
 * Provides endpoints for handling users' data and authentication flows.
 *
 * @author : Jim Wu
 */
@Controller
public class UsersController {

    // Service for managing user types (e.g., Admin, JobSeeker, Recruiter)
    private final UsersTypeService usersTypeService;

    // Service for handling user-related operations (e.g., adding, finding users)
    private final UsersService usersService;

    /**
     * Constructor for initializing the controller with required services.
     *
     * @param usersTypeService : Service for user type operations.
     * @param usersService : Service for user operations.
     */
    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    /**
     * Handles GET requests for user registration.
     *
     * @param model : Model object for passing data to the view.
     * @return : Returns the "register" view where users can fill in their registration details.
     */
    @GetMapping("/register")
    public String register(Model model) {
        // Retrieve all available user types (e.g., JobSeeker, Recruiter) and add them to the model.
        List<UsersType> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);

        // Add a new empty Users object to the model for binding form data.
        model.addAttribute("user", new Users());

        return "register";
    }

    /**
     * Handles POST requests for user registration.
     *
     * @param users : Users object containing the registration details.
     * @param model : Model object for passing data to the view.
     * @param redirectAttributes : Object for passing flash attributes (temporary data).
     * @return : Redirects to the dashboard upon successful registration or back to the registration page if an error occurs.
     */
    @PostMapping("/register/new")
    public String userRegistration(@Valid Users users, Model model, RedirectAttributes redirectAttributes) {
        // Check if the email provided by the user is already registered.
        Optional<Users> optionalUsers = this.usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            // If email is already registered, add an error message to redirect attributes and redirect to the registration page.
            redirectAttributes.addFlashAttribute("error", "Email already registered, try login or register with other email.");
            return "redirect:/register";
        }

        // Save the new user in the database.
        this.usersService.addNew(users);

        // Add the newly registered user to the model for rendering.
        model.addAttribute("users", users);

        // Redirect to the dashboard upon successful registration.
        return "redirect:/dashboard/";
    }

    /**
     * Handles GET requests for user login.
     *
     * @return : Returns the "login" view where users can log in.
     */
    @GetMapping("/login")
    public String login() {
        // Log message for debugging when the login endpoint is accessed.
        System.out.println("/login start up .......");
        return "login";
    }

    /**
     * Handles GET requests for user logout.
     *
     * @param request : HttpServletRequest object for managing the logout process.
     * @param response : HttpServletResponse object for managing the logout process.
     * @return : Redirects to the home page after logout.
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Log message for debugging when the logout endpoint is accessed.
        System.out.println("/logout start up .......");

        // Retrieve authentication details to check if the user is logged in.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If authentication exists, log out the user and invalidate their session.
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Redirect to the home page after logout.
        return "redirect:/";
    }
}