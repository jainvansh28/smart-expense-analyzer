package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.ExpenseRequest;
import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@Valid @RequestBody ExpenseRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Map<String, Object> result = expenseService.addExpense(userId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Expense expense = expenseService.updateExpense(id, userId, request);
            return ResponseEntity.ok(expense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            expenseService.deleteExpense(id, userId);
            return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> listExpenses(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<Expense> expenses = expenseService.getUserExpenses(userId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpense(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Expense expense = expenseService.getExpenseById(id, userId);
            return ResponseEntity.ok(expense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchExpenses(
            @RequestParam String q,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<Expense> expenses = expenseService.searchExpenses(userId, q);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/filter")
    public ResponseEntity<?> filterExpenses(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<Expense> expenses = expenseService.filterExpenses(
                userId, year, month, category, startDate, endDate);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/remove-anomaly")
    public ResponseEntity<?> removeAnomaly(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Expense expense = expenseService.removeAnomaly(id, userId);
            return ResponseEntity.ok(expense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
