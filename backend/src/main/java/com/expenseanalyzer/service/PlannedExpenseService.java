package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.PlannedExpenseRequest;
import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.model.PlannedExpense;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.PlannedExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlannedExpenseService {
    
    @Autowired
    private PlannedExpenseRepository plannedExpenseRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Transactional
    public PlannedExpense addPlannedExpense(Long userId, PlannedExpenseRequest request) {
        PlannedExpense planned = new PlannedExpense();
        planned.setUserId(userId);
        planned.setTitle(request.getTitle());
        planned.setAmount(request.getAmount());
        planned.setCategory(request.getCategory());
        planned.setDueDay(request.getDueDay());
        planned.setDescription(request.getDescription());
        planned.setIsPaid(false);
        
        return plannedExpenseRepository.save(planned);
    }
    
    public List<PlannedExpense> getUserPlannedExpenses(Long userId) {
        return plannedExpenseRepository.findByUserIdOrderByDueDayAsc(userId);
    }
    
    public List<PlannedExpense> getUpcomingPayments(Long userId) {
        return plannedExpenseRepository.findByUserIdAndIsPaidOrderByDueDayAsc(userId, false);
    }
    
    @Transactional
    public Expense markAsPaid(Long plannedExpenseId, Long userId) {
        PlannedExpense planned = plannedExpenseRepository.findById(plannedExpenseId)
                .orElseThrow(() -> new RuntimeException("Planned expense not found"));
        
        if (!planned.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (planned.getIsPaid()) {
            throw new RuntimeException("Already marked as paid");
        }
        
        // Create actual expense
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setAmount(planned.getAmount());
        expense.setCategory(planned.getCategory());
        expense.setDescription(planned.getTitle() + (planned.getDescription() != null ? " - " + planned.getDescription() : ""));
        expense.setDate(LocalDate.now());
        
        Expense savedExpense = expenseRepository.save(expense);
        
        // Mark planned expense as paid
        planned.setIsPaid(true);
        planned.setPaidDate(LocalDate.now());
        plannedExpenseRepository.save(planned);
        
        return savedExpense;
    }
    
    @Transactional
    public PlannedExpense updatePlannedExpense(Long plannedExpenseId, Long userId, PlannedExpenseRequest request) {
        PlannedExpense planned = plannedExpenseRepository.findById(plannedExpenseId)
                .orElseThrow(() -> new RuntimeException("Planned expense not found"));
        
        if (!planned.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        planned.setTitle(request.getTitle());
        planned.setAmount(request.getAmount());
        planned.setCategory(request.getCategory());
        planned.setDueDay(request.getDueDay());
        planned.setDescription(request.getDescription());
        
        return plannedExpenseRepository.save(planned);
    }
    
    @Transactional
    public void deletePlannedExpense(Long plannedExpenseId, Long userId) {
        PlannedExpense planned = plannedExpenseRepository.findById(plannedExpenseId)
                .orElseThrow(() -> new RuntimeException("Planned expense not found"));
        
        if (!planned.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        plannedExpenseRepository.delete(planned);
    }
}
