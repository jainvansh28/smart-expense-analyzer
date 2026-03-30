package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.BudgetStatusResponse;
import com.expenseanalyzer.dto.CategoryBudgetRequest;
import com.expenseanalyzer.model.CategoryBudget;
import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.repository.CategoryBudgetRepository;
import com.expenseanalyzer.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryBudgetService {
    
    @Autowired
    private CategoryBudgetRepository budgetRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Transactional
    public CategoryBudget setBudget(Long userId, CategoryBudgetRequest request) {
        Optional<CategoryBudget> existing = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, request.getCategory(), request.getMonth(), request.getYear());
        
        CategoryBudget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setBudgetAmount(request.getBudgetAmount());
        } else {
            budget = new CategoryBudget();
            budget.setUserId(userId);
            budget.setCategory(request.getCategory());
            budget.setBudgetAmount(request.getBudgetAmount());
            budget.setMonth(request.getMonth());
            budget.setYear(request.getYear());
        }
        
        return budgetRepository.save(budget);
    }
    
    public List<CategoryBudget> getUserBudgets(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }
    
    public List<BudgetStatusResponse> getCurrentMonthBudgetStatus(Long userId) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        List<CategoryBudget> budgets = budgetRepository.findByUserIdAndMonthAndYear(
                userId, currentMonth, currentYear);
        
        List<BudgetStatusResponse> statusList = new ArrayList<>();
        
        for (CategoryBudget budget : budgets) {
            // Get expenses for this category in current month
            List<Expense> expenses = expenseRepository.findByUserIdAndYearAndMonth(
                    userId, currentYear, currentMonth);
            
            BigDecimal spentAmount = expenses.stream()
                    .filter(e -> e.getCategory().equals(budget.getCategory()))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal remainingAmount = budget.getBudgetAmount().subtract(spentAmount);
            
            Double percentageUsed = BigDecimal.ZERO.compareTo(budget.getBudgetAmount()) != 0
                    ? spentAmount.divide(budget.getBudgetAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;
            
            Boolean isOverBudget = spentAmount.compareTo(budget.getBudgetAmount()) > 0;
            
            BudgetStatusResponse status = new BudgetStatusResponse(
                    budget.getId(),
                    budget.getCategory(),
                    budget.getBudgetAmount(),
                    spentAmount,
                    remainingAmount,
                    percentageUsed,
                    isOverBudget
            );
            
            statusList.add(status);
        }
        
        return statusList;
    }
    
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        CategoryBudget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        
        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        budgetRepository.delete(budget);
    }
}
