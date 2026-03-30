package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal currentBalance;
    private BigDecimal monthlyBudget;
    private BigDecimal remainingBudget;
    private Double budgetUsedPercentage;
}
