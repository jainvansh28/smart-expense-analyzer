package com.expenseanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "saving_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "goal_name", nullable = false, length = 100)
    private String goalName;
    
    @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetAmount;
    
    @Column(name = "current_amount", precision = 10, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @Column
    private LocalDate deadline;
    
    @Column(length = 20)
    private String status = "active";
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = "active";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
