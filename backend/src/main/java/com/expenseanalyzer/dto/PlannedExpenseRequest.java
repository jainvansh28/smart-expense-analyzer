package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlannedExpenseRequest {
    private String title;
    private BigDecimal amount;
    private String category;
    private Integer dueDay;
    private String description;
}
