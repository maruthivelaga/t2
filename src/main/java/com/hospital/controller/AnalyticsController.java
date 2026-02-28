package com.hospital.controller;

import com.hospital.dto.AppointmentAnalyticsDto;
import com.hospital.service.AppointmentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for appointment analytics and reports
 */
@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AppointmentAnalyticsService analyticsService;
    
    /**
     * Get comprehensive appointment analytics report (JSON endpoint)
     * @return Analytics report as JSON
     */
    @GetMapping("/appointments/report")
    @ResponseBody
    public ResponseEntity<AppointmentAnalyticsDto> getAppointmentAnalyticsJson() {
        AppointmentAnalyticsDto analytics = analyticsService.generateAnalyticsReport();
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Display appointment analytics report (HTML view)
     * @param model the model
     * @return View name
     */
    @GetMapping("/appointments")
    public String viewAppointmentAnalytics(Model model) {
        AppointmentAnalyticsDto analytics = analyticsService.generateAnalyticsReport();
        model.addAttribute("analytics", analytics);
        return "analytics/appointment-report";
    }
}
