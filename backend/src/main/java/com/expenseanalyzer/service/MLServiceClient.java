package com.expenseanalyzer.service;

import com.expenseanalyzer.model.Expense;
import com.expenseanalyzer.model.Prediction;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MLServiceClient {
    
    @Value("${ml.service.url}")
    private String mlServiceUrl;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private PredictionRepository predictionRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public Prediction getPrediction(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(userId);
        
        if (expenses.isEmpty()) {
            throw new RuntimeException("No expense data available for prediction");
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("expenses", expenses.stream()
                .map(e -> Map.of(
                        "amount", e.getAmount().doubleValue(),
                        "category", e.getCategory(),
                        "date", e.getDate().toString()
                ))
                .collect(Collectors.toList()));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    mlServiceUrl + "/predict",
                    request,
                    Map.class
            );
            
            Prediction prediction = new Prediction();
            prediction.setUserId(userId);
            prediction.setPredictedExpense(BigDecimal.valueOf((Double) response.get("predicted_expense")));
            prediction.setOverspendingRiskPercentage((Integer) response.get("overspending_risk_percentage"));
            prediction.setSavingsPrediction(BigDecimal.valueOf((Double) response.get("savings_prediction")));
            prediction.setPredictionDate(LocalDate.now().plusMonths(1));
            
            return predictionRepository.save(prediction);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get prediction from ML service: " + e.getMessage());
        }
    }
    
    public Prediction getLatestPrediction(Long userId) {
        return predictionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new RuntimeException("No predictions available"));
    }
}
