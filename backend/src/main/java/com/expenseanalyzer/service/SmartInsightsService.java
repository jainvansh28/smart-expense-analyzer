package com.expenseanalyzer.service;

import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.CategoryBudgetRepository;
import com.expenseanalyzer.repository.PlannedExpenseRepository;
import com.expenseanalyzer.repository.IncomeRepository;
import com.expenseanalyzer.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartInsightsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmartInsightsService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private CategoryBudgetRepository categoryBudgetRepository;
    
    @Autowired
    private PlannedExpenseRepository plannedExpenseRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private SavingGoalRepository savingGoalRepository;
    
    // 1. Monthly Spending Comparison
    public Map<String, Object> getMonthlyComparison(Long userId) {
        logger.info("Calculating monthly spending comparison for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        // Get previous month
        LocalDate previousMonthDate = today.minusMonths(1);
        int previousYear = previousMonthDate.getYear();
        int previousMonth = previousMonthDate.getMonthValue();
        
        // Calculate totals
        var currentExpenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        BigDecimal currentTotal = currentExpenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        var previousExpenses = expenseRepository.findByUserIdAndYearAndMonth(userId, previousYear, previousMonth);
        BigDecimal previousTotal = previousExpenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate percentage change
        double percentageChange = 0;
        String message;
        String trend = "stable";
        
        if (previousTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = currentTotal.subtract(previousTotal);
            percentageChange = change.divide(previousTotal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
            
            if (percentageChange > 0) {
                trend = "increased";
                message = String.format("Your spending increased by %.1f%% compared to last month.", Math.abs(percentageChange));
            } else if (percentageChange < 0) {
                trend = "decreased";
                message = String.format("Great! Your spending decreased by %.1f%% compared to last month.", Math.abs(percentageChange));
            } else {
                message = "Your spending is the same as last month.";
            }
        } else {
            message = "No previous month data available for comparison.";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("currentMonthTotal", currentTotal.doubleValue());
        result.put("previousMonthTotal", previousTotal.doubleValue());
        result.put("percentageChange", Math.round(percentageChange * 10) / 10.0);
        result.put("trend", trend);
        result.put("message", message);
        
        return result;
    }
    
    // 2. Category Budget Alerts
    public List<Map<String, Object>> getCategoryBudgetAlerts(Long userId) {
        logger.info("Checking category budget alerts for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        var budgets = categoryBudgetRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear);
        var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        
        // Group expenses by category
        Map<String, BigDecimal> categorySpending = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));
        
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        for (var budget : budgets) {
            BigDecimal spent = categorySpending.getOrDefault(budget.getCategory(), BigDecimal.ZERO);
            
            if (spent.compareTo(budget.getBudgetAmount()) > 0) {
                BigDecimal exceededBy = spent.subtract(budget.getBudgetAmount());
                
                Map<String, Object> alert = new HashMap<>();
                alert.put("category", budget.getCategory());
                alert.put("budget", budget.getBudgetAmount().doubleValue());
                alert.put("spent", spent.doubleValue());
                alert.put("exceededBy", exceededBy.doubleValue());
                alert.put("message", String.format("%s budget exceeded by ₹%.2f", budget.getCategory(), exceededBy.doubleValue()));
                
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
    
    // 3. Smart Expense Suggestions
    public List<Map<String, Object>> getSmartSuggestions(Long userId) {
        logger.info("Generating smart suggestions for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        
        // Group by category
        Map<String, BigDecimal> categorySpending = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));
        
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        if (!categorySpending.isEmpty()) {
            // Find highest spending category
            var highestCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
            
            if (highestCategory != null) {
                String category = highestCategory.getKey();
                double amount = highestCategory.getValue().doubleValue();
                
                Map<String, Object> suggestion = new HashMap<>();
                suggestion.put("type", "high_spending");
                suggestion.put("category", category);
                suggestion.put("amount", amount);
                suggestion.put("message", generateSuggestionMessage(category, amount));
                suggestions.add(suggestion);
            }
        }
        
        // Check for anomalies
        long anomalyCount = expenses.stream()
            .filter(expense -> Boolean.TRUE.equals(expense.getIsAnomaly()))
            .count();
        if (anomalyCount > 0) {
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("type", "anomaly_warning");
            suggestion.put("message", String.format("You have %d unusual expense(s) this month. Review them to ensure they're correct.", anomalyCount));
            suggestions.add(suggestion);
        }
        
        return suggestions;
    }
    
    private String generateSuggestionMessage(String category, double amount) {
        switch (category.toLowerCase()) {
            case "food":
                return String.format("Food spending is ₹%.2f this month. Try limiting restaurant expenses to save more.", amount);
            case "shopping":
                return String.format("You spent ₹%.2f on Shopping. Reducing it by ₹500 can improve savings.", amount);
            case "entertainment":
                return String.format("Entertainment expenses are ₹%.2f. Consider free activities to reduce costs.", amount);
            case "transport":
                return String.format("Transport costs are ₹%.2f. Carpooling or public transport can help save.", amount);
            default:
                return String.format("You spent most on %s (₹%.2f) this month. Consider reviewing these expenses.", category, amount);
        }
    }
    
    // 4. Upcoming Bill Reminders
    public List<Map<String, Object>> getUpcomingBillReminders(Long userId) {
        logger.info("Fetching upcoming bill reminders for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        
        var plannedExpenses = plannedExpenseRepository.findByUserIdAndIsPaidOrderByDueDayAsc(userId, false);
        
        List<Map<String, Object>> reminders = new ArrayList<>();
        
        for (var expense : plannedExpenses) {
            int dueDay = expense.getDueDay();
            int daysUntilDue;
            
            if (dueDay >= currentDay) {
                daysUntilDue = dueDay - currentDay;
            } else {
                // Due next month
                LocalDate nextDueDate = today.withDayOfMonth(1).plusMonths(1).withDayOfMonth(dueDay);
                daysUntilDue = (int) ChronoUnit.DAYS.between(today, nextDueDate);
            }
            
            if (daysUntilDue <= 3 && daysUntilDue >= 0) {
                Map<String, Object> reminder = new HashMap<>();
                reminder.put("id", expense.getId());
                reminder.put("title", expense.getTitle());
                reminder.put("amount", expense.getAmount().doubleValue());
                reminder.put("dueDay", dueDay);
                reminder.put("daysUntilDue", daysUntilDue);
                reminder.put("category", expense.getCategory());
                
                String message;
                if (daysUntilDue == 0) {
                    message = String.format("%s is due today!", expense.getTitle());
                } else if (daysUntilDue == 1) {
                    message = String.format("%s is due tomorrow!", expense.getTitle());
                } else {
                    message = String.format("%s is due in %d days.", expense.getTitle(), daysUntilDue);
                }
                reminder.put("message", message);
                
                reminders.add(reminder);
            }
        }
        
        return reminders;
    }
    
    // 5. Top 5 Expenses
    public List<Map<String, Object>> getTop5Expenses(Long userId) {
        logger.info("Fetching top 5 expenses for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        
        return expenses.stream()
            .sorted((e1, e2) -> e2.getAmount().compareTo(e1.getAmount()))
            .limit(5)
            .map(expense -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", expense.getId());
                map.put("amount", expense.getAmount().doubleValue());
                map.put("category", expense.getCategory());
                map.put("description", expense.getDescription());
                map.put("date", expense.getDate().toString());
                return map;
            })
            .collect(Collectors.toList());
    }
    
    // 6. Financial Health Score
    public Map<String, Object> getFinancialHealthScore(Long userId) {
        logger.info("Calculating financial health score for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        int score = 100;
        List<String> issues = new ArrayList<>();
        
        // Get data
        var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        BigDecimal totalExpenses = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalIncome = incomeRepository.getTotalIncomeByMonth(userId, currentYear, currentMonth);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;
        
        // Check spending percentage
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            double spendingPercentage = totalExpenses.divide(totalIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
            
            if (spendingPercentage >= 90) {
                score -= 30;
                issues.add("spending is very high");
            } else if (spendingPercentage >= 80) {
                score -= 20;
                issues.add("spending is high");
            } else if (spendingPercentage >= 70) {
                score -= 10;
            }
        }
        
        // Check budget violations
        var budgets = categoryBudgetRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear);
        Map<String, BigDecimal> categorySpending = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));
        
        int budgetViolations = 0;
        for (var budget : budgets) {
            BigDecimal spent = categorySpending.getOrDefault(budget.getCategory(), BigDecimal.ZERO);
            if (spent.compareTo(budget.getBudgetAmount()) > 0) {
                budgetViolations++;
            }
        }
        
        if (budgetViolations > 0) {
            score -= (budgetViolations * 10);
            issues.add(budgetViolations + " budget(s) exceeded");
        }
        
        // Check anomalies
        long anomalyCount = expenses.stream()
            .filter(expense -> Boolean.TRUE.equals(expense.getIsAnomaly()))
            .count();
        if (anomalyCount > 0) {
            score -= (int)(anomalyCount * 5);
            issues.add("unusual expenses detected");
        }
        
        // Check savings goals
        var savingGoals = savingGoalRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "active");
        if (!savingGoals.isEmpty()) {
            score += 10;
        }
        
        // Ensure score is within bounds
        score = Math.max(0, Math.min(100, score));
        
        String status;
        String message;
        
        if (score >= 80) {
            status = "Excellent";
            message = "Your finances are in great shape! Keep up the good work.";
        } else if (score >= 60) {
            status = "Good";
            message = "Your finances are under control";
            if (!issues.isEmpty()) {
                message += ", but " + String.join(" and ", issues) + ".";
            } else {
                message += ".";
            }
        } else if (score >= 40) {
            status = "Warning";
            message = "Your finances need attention: " + String.join(", ", issues) + ".";
        } else {
            status = "Risky";
            message = "Your finances are at risk: " + String.join(", ", issues) + ". Take immediate action.";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("status", status);
        result.put("message", message);
        
        return result;
    }
    
    // 7. Saving Streak
    public Map<String, Object> getSavingStreak(Long userId) {
        logger.info("Calculating saving streak for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int streak = 0;
        
        // Calculate daily safe limit (simple approach: monthly income / 30)
        BigDecimal monthlyIncome = incomeRepository.getTotalIncomeByMonth(userId, today.getYear(), today.getMonthValue());
        if (monthlyIncome == null) monthlyIncome = BigDecimal.ZERO;
        
        BigDecimal dailySafeLimit = monthlyIncome.compareTo(BigDecimal.ZERO) > 0 
            ? monthlyIncome.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP)
            : BigDecimal.valueOf(500); // Default safe limit
        
        // Check last 30 days
        for (int i = 0; i < 30; i++) {
            LocalDate checkDate = today.minusDays(i);
            LocalDate nextDay = checkDate.plusDays(1);
            
            var dayExpenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, checkDate, checkDate);
            BigDecimal dayTotal = dayExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (dayTotal.compareTo(dailySafeLimit) <= 0) {
                streak++;
            } else {
                break;
            }
        }
        
        String message;
        if (streak >= 7) {
            message = String.format("Amazing! You stayed within safe spending for %d days in a row. 🎉", streak);
        } else if (streak >= 3) {
            message = String.format("Good job! You stayed within safe spending for %d days. Keep it up!", streak);
        } else if (streak > 0) {
            message = String.format("You're on a %d-day streak. Keep going!", streak);
        } else {
            message = "Start your saving streak today by staying within your daily budget!";
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("streak", streak);
        result.put("message", message);
        result.put("dailySafeLimit", dailySafeLimit.doubleValue());
        
        return result;
    }
    
    // 8. Daily Spending Intensity (for heatmap)
    public List<Map<String, Object>> getDailySpendingIntensity(Long userId) {
        logger.info("Calculating daily spending intensity for user: {}", userId);
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        
        var expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, currentYear, currentMonth);
        
        // Group by date
        Map<LocalDate, BigDecimal> dailySpending = expenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getDate,
                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));
        
        // Find max spending for intensity calculation
        BigDecimal maxSpending = dailySpending.values().stream()
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ONE);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // Get all days in current month
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
        
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            BigDecimal spending = dailySpending.getOrDefault(date, BigDecimal.ZERO);
            
            // Calculate intensity (0-100)
            int intensity = maxSpending.compareTo(BigDecimal.ZERO) > 0
                ? spending.divide(maxSpending, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).intValue()
                : 0;
            
            String level;
            if (intensity == 0) {
                level = "none";
            } else if (intensity < 33) {
                level = "low";
            } else if (intensity < 66) {
                level = "medium";
            } else {
                level = "high";
            }
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("day", date.getDayOfMonth());
            dayData.put("amount", spending.doubleValue());
            dayData.put("intensity", intensity);
            dayData.put("level", level);
            
            result.add(dayData);
        }
        
        return result;
    }
    
    // Combined endpoint for all insights
    public Map<String, Object> getAllSmartInsights(Long userId) {
        logger.info("=== Fetching All Smart Insights for user: {} ===", userId);
        
        Map<String, Object> insights = new HashMap<>();
        
        try {
            insights.put("monthlyComparison", getMonthlyComparison(userId));
            insights.put("categoryBudgetAlerts", getCategoryBudgetAlerts(userId));
            insights.put("smartSuggestions", getSmartSuggestions(userId));
            insights.put("upcomingBillReminders", getUpcomingBillReminders(userId));
            insights.put("top5Expenses", getTop5Expenses(userId));
            insights.put("financialHealthScore", getFinancialHealthScore(userId));
            insights.put("savingStreak", getSavingStreak(userId));
            insights.put("dailySpendingIntensity", getDailySpendingIntensity(userId));
        } catch (Exception e) {
            logger.error("Error fetching smart insights: {}", e.getMessage(), e);
        }
        
        logger.info("=== Smart Insights Fetch Complete ===");
        return insights;
    }
}
