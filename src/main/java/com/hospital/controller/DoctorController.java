package com.hospital.controller;

import com.hospital.service.DoctorService;
import com.hospital.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for browsing doctors
 */
@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;
    
    /**
     * Browse all doctors with optional specialty filter
     * @param model Spring MVC model
     * @param specialtyId optional specialty filter
     * @return doctors browse view
     */
    @GetMapping
    public String browseDoctors(
            Model model,
            @RequestParam(required = false) Long specialtyId) {
        
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        
        if (specialtyId != null) {
            model.addAttribute("doctors", 
                    doctorService.getAvailableDoctorsBySpecialty(specialtyId));
            model.addAttribute("selectedSpecialtyId", specialtyId);
        } else {
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
        }
        
        return "doctors/browse";
    }
    
    /**
     * View doctor details
     * @param id doctor ID
     * @param model Spring MVC model
     * @return doctor details view
     */
    @GetMapping("/{id}")
    public String viewDoctor(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", 
                doctorService.getDoctorById(id)
                        .orElseThrow(() -> new RuntimeException("Doctor not found")));
        return "doctors/details";
    }
}
