package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.BalanceResponse;
import com.expenseanalyzer.dto.IncomeRequest;
import com.expenseanalyzer.model.Income;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class IncomeService {
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Transactional
    public Income addIncome(Long userId, IncomeRequest request) {
        Income income = new Income();
        income.setUserId(userId);
        income.setAmount(request.getAmount());
        income.setType(request.getType());
        income.setDescription(request.getDescription());
        income.setDate(request.getDate());
        
        return incomeRepository.save(income);
    }
    
    @Transactional
    public Income updateIncome(Long incomeId, Long userId, IncomeRequest request) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        income.setAmount(request.getAmount());
        income.setType(request.getType());
        income.setDescription(request.getDescription());
        income.setDate(request.getDate());
        
        return incomeRepository.save(income);
    }
    
    @Transactional
    public void deleteIncome(Long incomeId, Long userId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        incomeRepository.delete(income);
    }
    
    public List<Income> getUserIncome(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId);
    }
    
    public BalanceResponse getMonthlyBalance(Long userId, int year, int month) {
        System.out.println("=== getMonthlyBalance called ===");
        System.out.println("UserId: " + userId + ", Year: " + year + ", Month: " + month);
        
        // Get total income for the month
        BigDecimal totalIncome = incomeRepository.getTotalIncomeByMonth(userId, year, month);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        System.out.println("Total Income: " + totalIncome);
        
        // Get total expenses for the month - THIS SHOULD ALWAYS WORK
        List<com.expenseanalyzer.model.Expense> expenses = expenseRepository.findByUserIdAndYearAndMonth(userId, year, month);
        System.out.println("Expenses found: " + (expenses != null ? expenses.size() : 0));
        
        BigDecimal totalExpenses = BigDecimal.ZERO;
        if (expenses != null && !expenses.isEmpty()) {
            totalExpenses = expenses.stream()
                    .map(com.expenseanalyzer.model.Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        System.out.println("Total Expenses: " + totalExpenses);
        
        // Calculate current balance
        BigDecimal currentBalance = totalIncome.subtract(totalExpenses);
        
        // Set monthly budget (use income if available, otherwise use a default or just expenses)
        BigDecimal monthlyBudget = totalIncome.compareTo(BigDecimal.ZERO) > 0 ? totalIncome : totalExpenses;
        
        // Calculate remaining budget
        BigDecimal remainingBudget = totalIncome.subtract(totalExpenses);
        
        // Calculate budget used percentage
        Double budgetUsedPercentage = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            budgetUsedPercentage = totalExpenses
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            // If no income but there are expenses, show 100%
            budgetUsedPercentage = 100.0;
        }
        
        BalanceResponse response = new BalanceResponse(
                totalIncome,
                totalExpenses,
                currentBalance,
                monthlyBudget,
                remainingBudget,
                budgetUsedPercentage
        );
        
        System.out.println("Response: " + response);
        System.out.println("=== getMonthlyBalance end ===");
        
        return response;
    }
}
