package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
    
    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserIdAndDateGreaterThanEqualOrderByDateDesc(Long userId, LocalDate startDate);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month ORDER BY e.date DESC")
    List<Expense> findByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.userId = :userId AND YEAR(e.date) = :year AND MONTH(e.date) = :month GROUP BY e.category")
    List<Object[]> getCategoryWiseExpense(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
}
