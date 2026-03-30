package com.expenseanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "planned_expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlannedExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Column(name = "due_day", nullable = false)
    private Integer dueDay;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;
    
    @Column(name = "paid_date")
    private LocalDate paidDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPaid == null) {
            isPaid = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
