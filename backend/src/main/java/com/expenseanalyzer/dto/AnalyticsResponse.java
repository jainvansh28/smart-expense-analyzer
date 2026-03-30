package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private BigDecimal monthlyTotal;
    private Map<String, BigDecimal> categoryWiseSpending;
    private Map<String, Double> categoryPercentages;
    private BigDecimal previousMonthTotal;
    private Double monthOverMonthChange;
    private BigDecimal estimatedSavings;
    private Integer financialHealthScore;
    private List<String> suggestions;
}
