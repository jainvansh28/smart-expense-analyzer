package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.PlannedExpenseRequest;
import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.model.PlannedExpense;
import com.expenseanalyzer.service.PlannedExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planned-expenses")
public class PlannedExpenseController {
    
    @Autowired
    private PlannedExpenseService plannedExpenseService;
    
    @PostMapping
    public ResponseEntity<?> addPlannedExpense(
            @RequestBody PlannedExpenseRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            PlannedExpense planned = plannedExpenseService.addPlannedExpense(userId, request);
            return ResponseEntity.ok(planned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getPlannedExpenses(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<PlannedExpense> planned = plannedExpenseService.getUserPlannedExpenses(userId);
            return ResponseEntity.ok(planned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingPayments(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<PlannedExpense> upcoming = plannedExpenseService.getUpcomingPayments(userId);
            return ResponseEntity.ok(upcoming);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<?> markAsPaid(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Expense expense = plannedExpenseService.markAsPaid(id, userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Marked as paid and expense created",
                    "expense", expense
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlannedExpense(
            @PathVariable Long id,
            @RequestBody PlannedExpenseRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            PlannedExpense planned = plannedExpenseService.updatePlannedExpense(id, userId, request);
            return ResponseEntity.ok(planned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlannedExpense(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            plannedExpenseService.deletePlannedExpense(id, userId);
            return ResponseEntity.ok(Map.of("message", "Planned expense deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
