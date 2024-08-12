package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Thymeleaf template for login page
    }

    @PostMapping("/login")
    public ModelAndView handleLogin(@RequestParam String username, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView("login");

        try {
            User user = userService.findByUsername(username);

            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                modelAndView.addObject("message", "Login successful for user: " + username);
            } else {
                modelAndView.addObject("message", "Invalid username or password");
            }
        } catch (Exception e) {
            modelAndView.addObject("message", "An error occurred during login");
            e.printStackTrace(); // Log the error for debugging
        }

        return modelAndView;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Thymeleaf template for register page
    }

    @PostMapping("/register")
    public RedirectView handleRegister(@RequestParam String username, @RequestParam String password) {
        try {
            // Create a new user entity
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password)); // Hash the password

            // Save the user to the database
            userService.save(user);

            return new RedirectView("/login"); // Redirect to the login page after successful registration
        } catch (Exception e) {
            // Log the error and redirect to an error page or stay on the registration page
            e.printStackTrace(); // This will print the exception stack trace to the logs
            return new RedirectView("/register?error");
        }
    }
}
