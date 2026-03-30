package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.SavingGoalRequest;
import com.expenseanalyzer.model.SavingGoal;
import com.expenseanalyzer.service.SavingGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class SavingGoalController {
    
    @Autowired
    private SavingGoalService savingGoalService;
    
    @PostMapping
    public ResponseEntity<?> addGoal(
            @RequestBody SavingGoalRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            SavingGoal goal = savingGoalService.addGoal(userId, request);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getGoals(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<SavingGoal> goals = savingGoalService.getUserGoals(userId);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveGoals(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<SavingGoal> goals = savingGoalService.getActiveGoals(userId);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/add-progress")
    public ResponseEntity<?> addProgress(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            BigDecimal amount = request.get("amount");
            SavingGoal goal = savingGoalService.addProgress(id, userId, amount);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @PathVariable Long id,
            @RequestBody SavingGoalRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            SavingGoal goal = savingGoalService.updateGoal(id, userId, request);
            return ResponseEntity.ok(goal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            savingGoalService.deleteGoal(id, userId);
            return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
