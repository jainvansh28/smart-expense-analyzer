package com.expenseanalyzer.controller;

import com.expenseanalyzer.service.BudgetWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    
    @Autowired
    private BudgetWarningService budgetWarningService;
    
    @GetMapping("/budget-warning")
    public ResponseEntity<?> getBudgetWarning(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.error("Authentication is null or principal is null");
                return ResponseEntity.status(401)
                    .body(Map.of("error", "User not authenticated"));
            }
            
            Long userId = (Long) authentication.getPrincipal();
            logger.info("Fetching budget warning for user ID: {}", userId);
            
            Map<String, Object> warning = budgetWarningService.getBudgetWarning(userId);
            
            return ResponseEntity.ok(warning);
            
        } catch (Exception e) {
            logger.error("Error fetching budget warning: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to calculate budget warning"));
        }
    }
}
