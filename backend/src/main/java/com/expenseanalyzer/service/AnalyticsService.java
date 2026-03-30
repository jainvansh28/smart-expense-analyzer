package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.AnalyticsResponse;
import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public AnalyticsResponse getMonthlyAnalytics(Long userId, int year, int month) {
        System.out.println("=== getMonthlyAnalytics called ===");
        System.out.println("UserId: " + userId + ", Year: " + year + ", Month: " + month);
        
        List<Expense> currentMonthExpenses = expenseRepository.findByUserIdAndYearAndMonth(userId, year, month);
        System.out.println("Current month expenses found: " + currentMonthExpenses.size());
        
        BigDecimal monthlyTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Monthly Total: " + monthlyTotal);
        
        Map<String, BigDecimal> categoryWiseSpending = new HashMap<>();
        for (Expense expense : currentMonthExpenses) {
            categoryWiseSpending.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
        }
        
        Map<String, Double> categoryPercentages = new HashMap<>();
        if (monthlyTotal.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : categoryWiseSpending.entrySet()) {
                double percentage = entry.getValue()
                        .divide(monthlyTotal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                categoryPercentages.put(entry.getKey(), percentage);
            }
        }
        
        int previousMonth = month == 1 ? 12 : month - 1;
        int previousYear = month == 1 ? year - 1 : year;
        List<Expense> previousMonthExpenses = expenseRepository.findByUserIdAndYearAndMonth(userId, previousYear, previousMonth);
        
        BigDecimal previousMonthTotal = previousMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Double monthOverMonthChange = 0.0;
        if (previousMonthTotal.compareTo(BigDecimal.ZERO) > 0) {
            monthOverMonthChange = monthlyTotal.subtract(previousMonthTotal)
                    .divide(previousMonthTotal, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        
        BigDecimal estimatedIncome = BigDecimal.valueOf(50000);
        BigDecimal estimatedSavings = estimatedIncome.subtract(monthlyTotal);
        
        Integer financialHealthScore = calculateFinancialHealthScore(monthlyTotal, estimatedIncome, categoryWiseSpending);
        
        List<String> suggestions = generateSuggestions(categoryWiseSpending, monthOverMonthChange, previousMonthTotal, monthlyTotal);
        
        AnalyticsResponse response = new AnalyticsResponse();
        response.setMonthlyTotal(monthlyTotal);
        response.setCategoryWiseSpending(categoryWiseSpending);
        response.setCategoryPercentages(categoryPercentages);
        response.setPreviousMonthTotal(previousMonthTotal);
        response.setMonthOverMonthChange(monthOverMonthChange);
        response.setEstimatedSavings(estimatedSavings);
        response.setFinancialHealthScore(financialHealthScore);
        response.setSuggestions(suggestions);
        
        return response;
    }
    
    private Integer calculateFinancialHealthScore(BigDecimal monthlyTotal, BigDecimal estimatedIncome, Map<String, BigDecimal> categoryWiseSpending) {
        int score = 100;
        
        double spendingRatio = monthlyTotal.divide(estimatedIncome, 4, RoundingMode.HALF_UP).doubleValue();
        if (spendingRatio > 0.8) score -= 30;
        else if (spendingRatio > 0.6) score -= 20;
        else if (spendingRatio > 0.4) score -= 10;
        
        BigDecimal foodSpending = categoryWiseSpending.getOrDefault("Food", BigDecimal.ZERO);
        if (foodSpending.compareTo(monthlyTotal.multiply(BigDecimal.valueOf(0.3))) > 0) {
            score -= 15;
        }
        
        BigDecimal shoppingSpending = categoryWiseSpending.getOrDefault("Shopping", BigDecimal.ZERO);
        if (shoppingSpending.compareTo(monthlyTotal.multiply(BigDecimal.valueOf(0.25))) > 0) {
            score -= 15;
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private List<String> generateSuggestions(Map<String, BigDecimal> categoryWiseSpending, Double monthOverMonthChange, BigDecimal previousMonthTotal, BigDecimal currentMonthTotal) {
        List<String> suggestions = new ArrayList<>();
        
        if (monthOverMonthChange > 20) {
            suggestions.add("Your spending increased by " + String.format("%.1f", monthOverMonthChange) + "% this month. Consider reviewing your expenses.");
        }
        
        BigDecimal foodSpending = categoryWiseSpending.getOrDefault("Food", BigDecimal.ZERO);
        if (foodSpending.compareTo(BigDecimal.valueOf(5000)) > 0) {
            suggestions.add("Food spending is high. Try cooking at home more often to save money.");
        }
        
        BigDecimal travelSpending = categoryWiseSpending.getOrDefault("Travel", BigDecimal.ZERO);
        if (travelSpending.compareTo(BigDecimal.valueOf(4000)) > 0) {
            suggestions.add("Travel costs are unusually high. Consider carpooling or public transport.");
        }
        
        BigDecimal shoppingSpending = categoryWiseSpending.getOrDefault("Shopping", BigDecimal.ZERO);
        if (shoppingSpending.compareTo(BigDecimal.valueOf(8000)) > 0) {
            suggestions.add("Shopping expenses are high. Try to limit non-essential purchases.");
        }
        
        BigDecimal entertainmentSpending = categoryWiseSpending.getOrDefault("Entertainment", BigDecimal.ZERO);
        if (entertainmentSpending.compareTo(BigDecimal.valueOf(3000)) > 0) {
            suggestions.add("Entertainment spending is high. Look for free or low-cost alternatives.");
        }
        
        if (currentMonthTotal.compareTo(BigDecimal.valueOf(40000)) > 0) {
            suggestions.add("You may overspend next month. Try to set a budget and stick to it.");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("Great job! Your spending is under control. Keep it up!");
        }
        
        return suggestions;
    }
}
