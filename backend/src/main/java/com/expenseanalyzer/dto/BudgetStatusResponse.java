package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetStatusResponse {
    private Long id;
    private String category;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Double percentageUsed;
    private Boolean isOverBudget;
}
