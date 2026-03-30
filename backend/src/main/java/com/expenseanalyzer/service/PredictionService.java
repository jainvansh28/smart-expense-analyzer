package com.expenseanalyzer.service;

import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.model.Prediction;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.IncomeRepository;
import com.expenseanalyzer.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private PredictionRepository predictionRepository;
    
    public Map<String, Object> predictNextMonth(Long userId) {
        logger.info("=== Starting prediction generation for user ID: {} ===", userId);
        
        try {
            LocalDate today = LocalDate.now();
            LocalDate sixMonthsAgo = today.minusMonths(6);
            
            logger.info("Fetching expenses from {} to {}", sixMonthsAgo, today);
            
            // Fetch last 6 months of expenses using GreaterThanEqual
            List<Expense> expenses = expenseRepository.findByUserIdAndDateGreaterThanEqualOrderByDateDesc(
                userId, sixMonthsAgo
            );
            
            logger.info("Loaded {} expenses for prediction", expenses.size());
            
            // If no expenses, return helpful message
            if (expenses.isEmpty()) {
                logger.warn("No expense data found for user {}. Returning default prediction.", userId);
                return createDefaultPrediction(userId, today, "No expense history found. Start adding expenses to get AI predictions!");
            }
            
            // Calculate total spending
            BigDecimal totalSpending = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            logger.info("Total spending in last 6 months: ₹{}", totalSpending);
            
            // Group by month to count active months
            Set<YearMonth> activeMonths = expenses.stream()
                .map(e -> YearMonth.from(e.getDate()))
                .collect(Collectors.toSet());
            
            int numberOfMonths = Math.max(1, activeMonths.size());
            logger.info("Number of active months with expenses: {}", numberOfMonths);
            
            // Calculate average monthly spending
            BigDecimal averageMonthlySpending = totalSpending.divide(
                BigDecimal.valueOf(numberOfMonths), 
                2, 
                RoundingMode.HALF_UP
            );
            
            logger.info("Average monthly spending: ₹{}", averageMonthlySpending);
            
            // Group expenses by category
            Map<String, List<BigDecimal>> categoryExpenses = new HashMap<>();
            Map<String, BigDecimal> categoryTotals = new HashMap<>();
            
            for (Expense expense : expenses) {
                String category = expense.getCategory();
                BigDecimal amount = expense.getAmount();
                
                categoryExpenses.computeIfAbsent(category, k -> new ArrayList<>()).add(amount);
                categoryTotals.merge(category, amount, BigDecimal::add);
            }
            
            logger.info("Categories found: {}", categoryTotals.keySet());
            
            // Calculate category predictions based on percentage distribution
            List<Map<String, Object>> categoryPredictions = new ArrayList<>();
            
            for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
                String category = entry.getKey();
                BigDecimal categoryTotal = entry.getValue();
                
                // Calculate average for this category
                BigDecimal categoryAverage = categoryTotal.divide(
                    BigDecimal.valueOf(numberOfMonths),
                    2,
                    RoundingMode.HALF_UP
                );
                
                // Calculate percentage
                double percentage = totalSpending.compareTo(BigDecimal.ZERO) > 0
                    ? categoryTotal.divide(totalSpending, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;
                
                Map<String, Object> categoryPrediction = new HashMap<>();
                categoryPrediction.put("category", category);
                categoryPrediction.put("predictedAmount", categoryAverage.doubleValue());
                categoryPrediction.put("totalSpent", categoryTotal.doubleValue());
                categoryPrediction.put("percentage", Math.round(percentage));
                categoryPrediction.put("transactionCount", categoryExpenses.get(category).size());
                
                categoryPredictions.add(categoryPrediction);
                
                logger.info("Category {}: ₹{} ({}% of total)", 
                    category, categoryAverage, Math.round(percentage));
            }
            
            // Sort by predicted amount (highest first)
            categoryPredictions.sort((a, b) -> 
                Double.compare(
                    (Double) b.get("predictedAmount"), 
                    (Double) a.get("predictedAmount")
                )
            );
            
            // Calculate overspending risk and savings prediction
            BigDecimal totalIncome = incomeRepository.getTotalIncomeByUserId(userId);
            if (totalIncome == null) {
                totalIncome = BigDecimal.ZERO;
                logger.warn("No income data found for user {}", userId);
            }
            
            logger.info("Total historical income: ₹{}", totalIncome);
            
            // Calculate average monthly income for realistic savings prediction
            BigDecimal averageMonthlyIncome = totalIncome.compareTo(BigDecimal.ZERO) > 0 
                ? totalIncome.divide(BigDecimal.valueOf(numberOfMonths), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            
            logger.info("Average monthly income: ₹{}", averageMonthlyIncome);
            
            int overspendingRisk = calculateOverspendingRisk(averageMonthlySpending, averageMonthlyIncome);
            
            // Calculate savings prediction using monthly averages (can be negative if overspending)
            BigDecimal savingsPrediction = averageMonthlyIncome.subtract(averageMonthlySpending);
            
            logger.info("Predicted monthly savings: ₹{}", savingsPrediction);
            
            // Generate insights
            String insight = generateInsight(categoryPredictions, averageMonthlySpending, averageMonthlyIncome, numberOfMonths);
            
            // Save prediction to database
            try {
                Prediction prediction = new Prediction();
                prediction.setUserId(userId);
                prediction.setPredictedExpense(averageMonthlySpending);
                prediction.setOverspendingRiskPercentage(overspendingRisk);
                prediction.setSavingsPrediction(savingsPrediction);
                prediction.setPredictionDate(today.plusMonths(1));
                predictionRepository.save(prediction);
                logger.info("Prediction saved successfully to database");
            } catch (Exception e) {
                logger.error("Failed to save prediction to database: {}", e.getMessage());
                // Continue even if save fails
            }
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("totalPredictedExpense", averageMonthlySpending.doubleValue());
            response.put("predictedExpense", averageMonthlySpending.doubleValue()); // For backward compatibility
            response.put("categoryPredictions", categoryPredictions);
            response.put("overspendingRiskPercentage", overspendingRisk);
            response.put("savingsPrediction", savingsPrediction.doubleValue());
            response.put("insight", insight);
            response.put("predictionMonth", today.plusMonths(1).getMonth().toString());
            response.put("predictionYear", today.plusMonths(1).getYear());
            response.put("historicalMonths", numberOfMonths);
            response.put("totalHistoricalSpending", totalSpending.doubleValue());
            
            logger.info("=== Prediction generated successfully ===");
            logger.info("Total Predicted: ₹{}, Risk: {}%, Monthly Savings: ₹{}", 
                averageMonthlySpending, overspendingRisk, savingsPrediction);
            
            return response;
            
        } catch (Exception e) {
            logger.error("=== EXCEPTION in PredictionService ===");
            logger.error("User ID: {}", userId);
            logger.error("Exception Type: {}", e.getClass().getName());
            logger.error("Exception Message: {}", e.getMessage());
            logger.error("Stack Trace:", e);
            
            // Return default prediction instead of throwing error
            return createDefaultPrediction(userId, LocalDate.now(), 
                "Unable to generate prediction: " + e.getMessage());
        }
    }
    
    private Map<String, Object> createDefaultPrediction(Long userId, LocalDate today, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("totalPredictedExpense", 0.0);
        response.put("predictedExpense", 0.0);
        response.put("categoryPredictions", new ArrayList<>());
        response.put("overspendingRiskPercentage", 0);
        response.put("savingsPrediction", 0.0);
        response.put("insight", message);
        response.put("predictionMonth", today.plusMonths(1).getMonth().toString());
        response.put("predictionYear", today.plusMonths(1).getYear());
        
        logger.info("Returning default prediction for user {}: {}", userId, message);
        return response;
    }
    
    private int calculateOverspendingRisk(BigDecimal predictedExpense, BigDecimal monthlyIncome) {
        if (monthlyIncome.compareTo(BigDecimal.ZERO) == 0) {
            return predictedExpense.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0;
        }
        
        BigDecimal ratio = predictedExpense.divide(monthlyIncome, 4, RoundingMode.HALF_UP);
        int risk = ratio.multiply(BigDecimal.valueOf(100)).intValue();
        
        return Math.min(100, Math.max(0, risk));
    }
    
    private String generateInsight(List<Map<String, Object>> categoryPredictions, 
                                   BigDecimal totalPredicted, 
                                   BigDecimal monthlyIncome,
                                   int numberOfMonths) {
        if (categoryPredictions.isEmpty()) {
            return "Start tracking expenses to get personalized insights.";
        }
        
        Map<String, Object> topCategory = categoryPredictions.get(0);
        String topCategoryName = (String) topCategory.get("category");
        Number topPercentageNum = (Number) topCategory.get("percentage");
        int topPercentage = topPercentageNum.intValue();
        
        StringBuilder insight = new StringBuilder();
        insight.append(String.format("Based on %d months of data, you usually spend most on %s (%d%% of total). ", 
            numberOfMonths, topCategoryName, topPercentage));
        
        // Check second category
        if (categoryPredictions.size() > 1) {
            Map<String, Object> secondCategory = categoryPredictions.get(1);
            String secondCategoryName = (String) secondCategory.get("category");
            Number secondPercentageNum = (Number) secondCategory.get("percentage");
            int secondPercentage = secondPercentageNum.intValue();
            insight.append(String.format("%s expenses are also significant at %d%%. ", 
                secondCategoryName, secondPercentage));
        }
        
        // Income vs expense insight using monthly averages
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = totalPredicted.divide(monthlyIncome, 2, RoundingMode.HALF_UP);
            if (ratio.compareTo(BigDecimal.valueOf(0.9)) > 0) {
                insight.append("⚠️ You're spending close to your monthly income limit. Consider reducing expenses.");
            } else if (ratio.compareTo(BigDecimal.valueOf(0.7)) > 0) {
                insight.append("You're spending moderately. Try to save more if possible.");
            } else {
                insight.append("✅ Great job! You're maintaining healthy spending habits.");
            }
        } else {
            insight.append("Add income data to get better financial insights.");
        }
        
        return insight.toString();
    }
    
    public Prediction getLatestPrediction(Long userId) {
        try {
            return predictionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);
        } catch (Exception e) {
            logger.error("Error fetching latest prediction for user {}: {}", userId, e.getMessage());
            return null;
        }
    }
}
