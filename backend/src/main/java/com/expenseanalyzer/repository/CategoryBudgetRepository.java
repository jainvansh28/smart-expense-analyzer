package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {
    List<CategoryBudget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    
    Optional<CategoryBudget> findByUserIdAndCategoryAndMonthAndYear(Long userId, String category, Integer month, Integer year);
    
    @Query("SELECT cb FROM CategoryBudget cb WHERE cb.userId = :userId AND cb.year = :year ORDER BY cb.month DESC, cb.category ASC")
    List<CategoryBudget> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") Integer year);
}
