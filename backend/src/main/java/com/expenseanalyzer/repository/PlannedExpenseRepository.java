package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.PlannedExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlannedExpenseRepository extends JpaRepository<PlannedExpense, Long> {
    List<PlannedExpense> findByUserIdAndIsPaidOrderByDueDayAsc(Long userId, Boolean isPaid);
    
    List<PlannedExpense> findByUserIdOrderByDueDayAsc(Long userId);
}
