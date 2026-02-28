package com.hospital.controller;

import com.hospital.dto.PatientRegistrationDto;
import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for patient registration
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    
    private final PatientService patientService;
    
    /**
     * Show registration form
     * @param model Spring MVC model
     * @return registration view
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new PatientRegistrationDto());
        return "register";
    }
    
    /**
     * Process registration form submission
     * @param registrationDto patient registration data
     * @param bindingResult validation result
     * @param redirectAttributes redirect attributes for flash messages
     * @param model Spring MVC model
     * @return redirect to login on success, or back to registration on error
     */
    @PostMapping("/register")
    public String registerPatient(
            @Valid @ModelAttribute("registrationDto") PatientRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        try {
            Patient patient = patientService.registerPatient(registrationDto);
            log.info("Patient registered successfully: {}", patient.getFullName());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
