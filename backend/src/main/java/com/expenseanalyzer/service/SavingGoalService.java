package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.SavingGoalRequest;
import com.expenseanalyzer.model.SavingGoal;
import com.expenseanalyzer.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SavingGoalService {
    
    @Autowired
    private SavingGoalRepository savingGoalRepository;
    
    @Transactional
    public SavingGoal addGoal(Long userId, SavingGoalRequest request) {
        SavingGoal goal = new SavingGoal();
        goal.setUserId(userId);
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO);
        goal.setDeadline(request.getDeadline());
        goal.setStatus("active");
        
        return savingGoalRepository.save(goal);
    }
    
    public List<SavingGoal> getUserGoals(Long userId) {
        return savingGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<SavingGoal> getActiveGoals(Long userId) {
        return savingGoalRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "active");
    }
    
    @Transactional
    public SavingGoal addProgress(Long goalId, Long userId, BigDecimal amount) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);
        
        // Check if goal is completed
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("completed");
        }
        
        return savingGoalRepository.save(goal);
    }
    
    @Transactional
    public SavingGoal updateGoal(Long goalId, Long userId, SavingGoalRequest request) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        if (request.getCurrentAmount() != null) {
            goal.setCurrentAmount(request.getCurrentAmount());
        }
        goal.setDeadline(request.getDeadline());
        
        return savingGoalRepository.save(goal);
    }
    
    @Transactional
    public void deleteGoal(Long goalId, Long userId) {
        SavingGoal goal = savingGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        
        if (!goal.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        savingGoalRepository.delete(goal);
    }
}
