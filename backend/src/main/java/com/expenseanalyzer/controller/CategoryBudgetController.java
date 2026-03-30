package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.BudgetStatusResponse;
import com.expenseanalyzer.dto.CategoryBudgetRequest;
import com.expenseanalyzer.model.CategoryBudget;
import com.expenseanalyzer.service.CategoryBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class CategoryBudgetController {
    
    @Autowired
    private CategoryBudgetService budgetService;
    
    @PostMapping
    public ResponseEntity<?> setBudget(
            @RequestBody CategoryBudgetRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            CategoryBudget budget = budgetService.setBudget(userId, request);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            LocalDate now = LocalDate.now();
            int targetMonth = month != null ? month : now.getMonthValue();
            int targetYear = year != null ? year : now.getYear();
            
            List<CategoryBudget> budgets = budgetService.getUserBudgets(userId, targetMonth, targetYear);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/current-month")
    public ResponseEntity<?> getCurrentMonthBudgetStatus(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<BudgetStatusResponse> status = budgetService.getCurrentMonthBudgetStatus(userId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            budgetService.deleteBudget(id, userId);
            return ResponseEntity.ok(Map.of("message", "Budget deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
