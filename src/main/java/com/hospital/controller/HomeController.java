package com.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for public pages (home, login, etc.)
 */
@Controller
public class HomeController {
    
    /**
     * Home page mapping
     * @return home view
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
    
    /**
     * Login page mapping
     * @return login view
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * Access denied page
     * @return access-denied view
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
