package com.expenseanalyzer.controller;

import com.expenseanalyzer.dto.BalanceResponse;
import com.expenseanalyzer.dto.IncomeRequest;
import com.expenseanalyzer.model.Income;
import com.expenseanalyzer.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/income")
public class IncomeController {
    
    @Autowired
    private IncomeService incomeService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addIncome(@Valid @RequestBody IncomeRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Income income = incomeService.addIncome(userId, request);
            return ResponseEntity.ok(income);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRequest request, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Income income = incomeService.updateIncome(id, userId, request);
            return ResponseEntity.ok(income);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            incomeService.deleteIncome(id, userId);
            return ResponseEntity.ok(Map.of("message", "Income deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> listIncome(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<Income> income = incomeService.getUserIncome(userId);
            return ResponseEntity.ok(income);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            LocalDate now = LocalDate.now();
            int targetYear = year != null ? year : now.getYear();
            int targetMonth = month != null ? month : now.getMonthValue();
            
            BalanceResponse balance = incomeService.getMonthlyBalance(userId, targetYear, targetMonth);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
