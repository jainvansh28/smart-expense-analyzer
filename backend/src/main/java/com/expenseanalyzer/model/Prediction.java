package com.expenseanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "predicted_expense", nullable = false, precision = 10, scale = 2)
    private BigDecimal predictedExpense;
    
    @Column(name = "overspending_risk_percentage", nullable = false)
    private Integer overspendingRiskPercentage;
    
    @Column(name = "savings_prediction", nullable = false, precision = 10, scale = 2)
    private BigDecimal savingsPrediction;
    
    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
