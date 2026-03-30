package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBudgetRequest {
    private String category;
    private BigDecimal budgetAmount;
    private Integer month;
    private Integer year;
}
