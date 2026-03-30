package com.expenseanalyzer.controller;

import com.expenseanalyzer.model.Prediction;
import com.expenseanalyzer.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prediction")
public class PredictionController {
    
    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);
    
    @Autowired
    private PredictionService predictionService;
    
    @GetMapping("/next-month")
    public ResponseEntity<?> getNextMonthPrediction(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.error("Authentication is null or principal is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            Long userId = (Long) authentication.getPrincipal();
            logger.info("=== Controller: Generating prediction for user ID: {} ===", userId);
            
            Map<String, Object> prediction = predictionService.predictNextMonth(userId);
            
            logger.info("=== Controller: Prediction generated successfully ===");
            logger.info("Total Predicted Expense: {}", prediction.get("totalPredictedExpense"));
            logger.info("Category Predictions Count: {}", 
                prediction.get("categoryPredictions") != null ? 
                ((java.util.List<?>) prediction.get("categoryPredictions")).size() : 0);
            
            return ResponseEntity.ok(prediction);
            
        } catch (Exception e) {
            logger.error("=== Controller: EXCEPTION in prediction generation ===");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Stack trace:", e);
            
            // Return error response but don't hide the real error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Prediction generation failed: " + e.getMessage());
            errorResponse.put("totalPredictedExpense", 0.0);
            errorResponse.put("predictedExpense", 0.0);
            errorResponse.put("categoryPredictions", new java.util.ArrayList<>());
            errorResponse.put("overspendingRiskPercentage", 0);
            errorResponse.put("savingsPrediction", 0.0);
            errorResponse.put("insight", "Unable to generate prediction. Error: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPrediction(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.error("Authentication is null or principal is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User not authenticated"));
            }
            
            Long userId = (Long) authentication.getPrincipal();
            logger.info("Fetching latest prediction for user ID: {}", userId);
            
            Prediction prediction = predictionService.getLatestPrediction(userId);
            
            if (prediction == null) {
                logger.info("No prediction found for user {}", userId);
                return ResponseEntity.ok(createDefaultPrediction("No predictions available yet"));
            }
            
            return ResponseEntity.ok(prediction);
            
        } catch (Exception e) {
            logger.error("Error fetching latest prediction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.OK)
                .body(createDefaultPrediction(e.getMessage()));
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
    
    private Map<String, Object> createDefaultPrediction(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("totalPredictedExpense", 0.0);
        response.put("predictedExpense", 0.0);
        response.put("categoryPredictions", new java.util.ArrayList<>());
        response.put("overspendingRiskPercentage", 0);
        response.put("savingsPrediction", 0.0);
        response.put("insight", "Start tracking your expenses to get personalized predictions.");
        response.put("message", message);
        return response;
    }
}
