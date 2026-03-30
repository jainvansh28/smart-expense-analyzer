package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.AnalyticsResponse;
import com.expenseanalyzer.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyAnalytics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            LocalDate now = LocalDate.now();
            int targetYear = year != null ? year : now.getYear();
            int targetMonth = month != null ? month : now.getMonthValue();
            
            AnalyticsResponse analytics = analyticsService.getMonthlyAnalytics(userId, targetYear, targetMonth);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
