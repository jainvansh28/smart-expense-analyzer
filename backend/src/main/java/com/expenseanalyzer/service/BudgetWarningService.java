package com.expenseanalyzer.service;

import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class BudgetWarningService {
    
    private static final Logger logger = LoggerFactory.getLogger(BudgetWarningService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    public Map<String, Object> getBudgetWarning(Long userId) {
        logger.info("=== AI Budget Warning System Started ===");
        logger.info("Calculating budget warning for user ID: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate today = LocalDate.now();
            int currentYear = today.getYear();
            int currentMonth = today.getMonthValue();
            
            // Get current month's income
            BigDecimal monthlyIncome = incomeRepository.getTotalIncomeByMonth(userId, currentYear, currentMonth);
            if (monthlyIncome == null) {
                monthlyIncome = BigDecimal.ZERO;
            }
            
            logger.info("Monthly Income: ₹{}", monthlyIncome);
            
            // Get current month's expenses
            var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
            BigDecimal monthlyExpenses = expenses.stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            logger.info("Monthly Expenses: ₹{}", monthlyExpenses);
            
            // Calculate spending percentage
            int spendingPercentage = 0;
            if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = monthlyExpenses
                    .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                spendingPercentage = percentage.intValue();
            } else if (monthlyExpenses.compareTo(BigDecimal.ZERO) > 0) {
                // Has expenses but no income
                spendingPercentage = 100;
            }
            
            logger.info("Spending Percentage: {}%", spendingPercentage);
            
            // Determine warning level
            String warningLevel;
            if (spendingPercentage < 60) {
                warningLevel = "SAFE";
            } else if (spendingPercentage < 80) {
                warningLevel = "MEDIUM";
            } else {
                warningLevel = "HIGH";
            }
            
            logger.info("Warning Level: {}", warningLevel);
            
            // Find top spending category
            Map<String, BigDecimal> categoryTotals = new HashMap<>();
            for (var expense : expenses) {
                categoryTotals.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
            }
            
            String topCategory = "Unknown";
            BigDecimal topAmount = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
                if (entry.getValue().compareTo(topAmount) > 0) {
                    topAmount = entry.getValue();
                    topCategory = entry.getKey();
                }
            }
            
            logger.info("Top Spending Category: {} (₹{})", topCategory, topAmount);
            
            // Generate message
            String message = generateMessage(warningLevel, spendingPercentage, topCategory, monthlyIncome);
            
            // Build response
            response.put("spendingPercentage", spendingPercentage);
            response.put("warningLevel", warningLevel);
            response.put("topCategory", topCategory);
            response.put("message", message);
            response.put("monthlyIncome", monthlyIncome.doubleValue());
            response.put("monthlyExpenses", monthlyExpenses.doubleValue());
            response.put("remainingBudget", monthlyIncome.subtract(monthlyExpenses).doubleValue());
            
            logger.info("=== AI Budget Warning System Completed ===");
            
        } catch (Exception e) {
            logger.error("Error calculating budget warning: {}", e.getMessage(), e);
            // Return safe default on error
            response.put("spendingPercentage", 0);
            response.put("warningLevel", "SAFE");
            response.put("topCategory", "Unknown");
            response.put("message", "Unable to calculate budget warning. Add income and expenses to get insights.");
            response.put("monthlyIncome", 0.0);
            response.put("monthlyExpenses", 0.0);
            response.put("remainingBudget", 0.0);
        }
        
        return response;
    }
    
    private String generateMessage(String warningLevel, int percentage, String topCategory, BigDecimal income) {
        StringBuilder message = new StringBuilder();
        
        switch (warningLevel) {
            case "SAFE":
                message.append("✅ Great! Your spending is under control. ");
                message.append(String.format("You've used %d%% of your monthly income. ", percentage));
                if (!topCategory.equals("Unknown")) {
                    message.append(String.format("Top spending: %s. ", topCategory));
                }
                message.append("Keep up the good work!");
                break;
                
            case "MEDIUM":
                message.append("⚡ Budget Alert: ");
                message.append(String.format("You have used %d%% of your monthly income. ", percentage));
                if (!topCategory.equals("Unknown")) {
                    message.append(String.format("Most spending is on %s. ", topCategory));
                }
                message.append("Consider monitoring your expenses closely.");
                break;
                
            case "HIGH":
                message.append("⚠️ High Budget Alert: ");
                message.append(String.format("You have used %d%% of your monthly income! ", percentage));
                if (!topCategory.equals("Unknown")) {
                    message.append(String.format("Most spending is on %s. ", topCategory));
                }
                message.append("Consider reducing non-essential spending to avoid overspending.");
                break;
                
            default:
                message.append("Budget status unavailable.");
        }
        
        return message.toString();
    }
}
