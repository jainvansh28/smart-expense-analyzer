package com.expenseanalyzer.service;

import com.expenseanalyzer.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnomalyDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionService.class);
    private static final double ANOMALY_THRESHOLD = 2.5;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public Map<String, Object> detectAnomaly(Long userId, String category, BigDecimal amount) {
        logger.info("=== AI Anomaly Detection Started ===");
        logger.info("User ID: {}, Category: {}, Amount: ₹{}", userId, category, amount);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Get expenses from last 6 months for this category
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            LocalDate today = LocalDate.now();
            
            var expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                userId, sixMonthsAgo, today
            );
            
            // Filter by category and calculate average
            var categoryExpenses = expenses.stream()
                .filter(e -> e.getCategory().equals(category))
                .map(e -> e.getAmount())
                .toList();
            
            logger.info("Found {} historical expenses in category: {}", categoryExpenses.size(), category);
            
            if (categoryExpenses.isEmpty()) {
                // No historical data, cannot detect anomaly
                logger.info("No historical data for category {}. Skipping anomaly detection.", category);
                result.put("isAnomaly", false);
                result.put("anomalyMessage", null);
                return result;
            }
            
            // Calculate average
            BigDecimal sum = categoryExpenses.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sum.divide(
                BigDecimal.valueOf(categoryExpenses.size()),
                2,
                java.math.RoundingMode.HALF_UP
            );
            
            logger.info("Average spending for {}: ₹{}", category, average);
            
            // Check if current expense is anomaly (> 2.5x average)
            BigDecimal threshold = average.multiply(BigDecimal.valueOf(ANOMALY_THRESHOLD));
            boolean isAnomaly = amount.compareTo(threshold) > 0;
            
            logger.info("Threshold ({}x average): ₹{}", ANOMALY_THRESHOLD, threshold);
            logger.info("New Expense: ₹{}", amount);
            logger.info("Anomaly Detected: {}", isAnomaly);
            
            result.put("isAnomaly", isAnomaly);
            
            if (isAnomaly) {
                String message = String.format(
                    "⚠️ Unusual spending detected! You spent ₹%s on %s, which is %.1fx higher than your usual spending of ₹%s.",
                    amount,
                    category,
                    amount.divide(average, 1, java.math.RoundingMode.HALF_UP).doubleValue(),
                    average
                );
                result.put("anomalyMessage", message);
                result.put("averageAmount", average.doubleValue());
                result.put("currentAmount", amount.doubleValue());
                result.put("multiplier", amount.divide(average, 1, java.math.RoundingMode.HALF_UP).doubleValue());
                
                logger.warn("ANOMALY ALERT: {}", message);
            } else {
                result.put("anomalyMessage", null);
                logger.info("Expense is within normal range.");
            }
            
            logger.info("=== AI Anomaly Detection Completed ===");
            
        } catch (Exception e) {
            logger.error("Error in anomaly detection: {}", e.getMessage(), e);
            // On error, return no anomaly to not block expense creation
            result.put("isAnomaly", false);
            result.put("anomalyMessage", null);
        }
        
        return result;
    }
}
