package com.hospital.controller;

import com.hospital.dto.AppointmentRequestDto;
import com.hospital.model.Appointment;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for appointment management
 */
@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final SpecialtyService specialtyService;
    
    /**
     * View all appointments for current patient
     * @param model Spring MVC model
     * @param authentication current authentication
     * @return appointments list view
     */
    @GetMapping
    public String viewAppointments(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("appointments", 
                appointmentService.getPatientAppointments(username));
        return "appointments/list";
    }
    
    /**
     * Show new appointment booking form
     * @param model Spring MVC model
     * @param specialtyId optional specialty filter
     * @return booking form view
     */
    @GetMapping("/book")
    public String showBookingForm(
            Model model,
            @RequestParam(required = false) Long specialtyId) {
        
        model.addAttribute("appointmentRequest", new AppointmentRequestDto());
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        
        if (specialtyId != null) {
            model.addAttribute("doctors", 
                    doctorService.getAvailableDoctorsBySpecialty(specialtyId));
            model.addAttribute("selectedSpecialtyId", specialtyId);
        } else {
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
        }
        
        return "appointments/book";
    }
    
    /**
     * Process appointment booking
     * @param appointmentRequest appointment booking data
     * @param bindingResult validation result
     * @param authentication current authentication
     * @param redirectAttributes redirect attributes for flash messages
     * @param model Spring MVC model
     * @return redirect to appointments list on success
     */
    @PostMapping("/book")
    public String bookAppointment(
            @Valid @ModelAttribute("appointmentRequest") AppointmentRequestDto appointmentRequest,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialties", specialtyService.getAllSpecialties());
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
            return "appointments/book";
        }
        
        try {
            String username = authentication.getName();
            Appointment appointment = appointmentService.bookAppointment(
                    appointmentRequest, username);
            
            log.info("Appointment booked successfully: ID {}", appointment.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment booked successfully!");
            return "redirect:/appointments";
            
        } catch (RuntimeException e) {
            log.error("Failed to book appointment: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("specialties", specialtyService.getAllSpecialties());
            model.addAttribute("doctors", doctorService.getAvailableDoctors());
            return "appointments/book";
        }
    }
    
    /**
     * View appointment details
     * @param id appointment ID
     * @param model Spring MVC model
     * @param authentication current authentication
     * @return appointment details view
     */
    @GetMapping("/{id}")
    public String viewAppointment(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {
        
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Verify the appointment belongs to the current patient
        String username = authentication.getName();
        if (!appointment.getPatient().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        model.addAttribute("appointment", appointment);
        return "appointments/details";
    }
    
    /**
     * Cancel an appointment
     * @param id appointment ID
     * @param authentication current authentication
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to appointments list
     */
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String username = authentication.getName();
            appointmentService.cancelAppointment(id, username);
            
            log.info("Appointment {} cancelled by {}", id, username);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment cancelled successfully");
            
        } catch (RuntimeException e) {
            log.error("Failed to cancel appointment: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/appointments";
    }
}
