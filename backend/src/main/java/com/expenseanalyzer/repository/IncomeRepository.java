package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByDateDesc(Long userId);
    
    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND YEAR(i.date) = :year AND MONTH(i.date) = :month ORDER BY i.date DESC")
    List<Income> findByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.userId = :userId AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    BigDecimal getTotalIncomeByMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.userId = :userId")
    BigDecimal getTotalIncomeByUserId(@Param("userId") Long userId);
}
