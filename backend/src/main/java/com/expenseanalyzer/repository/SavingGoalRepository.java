package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {
    List<SavingGoal> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    
    List<SavingGoal> findByUserIdOrderByCreatedAtDesc(Long userId);
}
