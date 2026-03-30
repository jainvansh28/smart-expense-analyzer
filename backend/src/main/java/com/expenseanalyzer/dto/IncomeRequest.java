package com.expenseanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Type is required")
    private String type; // 'salary' or 'extra'
    
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
}
