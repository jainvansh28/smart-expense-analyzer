package com.expenseanalyzer.controller;

import com.expenseanalyzer.service.SmartInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "*")
public class SmartInsightsController {
    
    @Autowired
    private SmartInsightsService smartInsightsService;
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllInsights(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Map<String, Object> insights = smartInsightsService.getAllSmartInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/monthly-comparison")
    public ResponseEntity<?> getMonthlyComparison(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getMonthlyComparison(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/budget-alerts")
    public ResponseEntity<?> getBudgetAlerts(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getCategoryBudgetAlerts(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getSmartSuggestions(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/bill-reminders")
    public ResponseEntity<?> getBillReminders(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getUpcomingBillReminders(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/top-expenses")
    public ResponseEntity<?> getTopExpenses(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getTop5Expenses(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/health-score")
    public ResponseEntity<?> getHealthScore(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getFinancialHealthScore(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/saving-streak")
    public ResponseEntity<?> getSavingStreak(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getSavingStreak(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/spending-heatmap")
    public ResponseEntity<?> getSpendingHeatmap(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(smartInsightsService.getDailySpendingIntensity(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
