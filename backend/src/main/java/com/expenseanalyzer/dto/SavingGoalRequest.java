package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoalRequest {
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;
}
