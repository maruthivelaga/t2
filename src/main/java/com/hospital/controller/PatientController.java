package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for patient-specific pages
 */
@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
@Slf4j
public class PatientController {
    
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    
    /**
     * Patient dashboard
     * @param model Spring MVC model
     * @param authentication current authentication
     * @return dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        
        Patient patient = patientService.getPatientByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        model.addAttribute("patient", patient);
        model.addAttribute("upcomingAppointments", 
                appointmentService.getUpcomingAppointments(username));
        
        return "patient/dashboard";
    }
    
    /**
     * View patient profile
     * @param model Spring MVC model
     * @param authentication current authentication
     * @return profile view
     */
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();
        
        Patient patient = patientService.getPatientByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        model.addAttribute("patient", patient);
        return "patient/profile";
    }
}
