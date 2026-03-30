package com.expenseanalyzer.repository;

import com.expenseanalyzer.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Optional<Prediction> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Prediction> findByUserIdAndPredictionDate(Long userId, LocalDate predictionDate);
}
