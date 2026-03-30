package com.expenseanalyzer.service;

import com.expenseanalyzer.model.User;
import com.expenseanalyzer.repository.UserRepository;
import com.expenseanalyzer.repository.ExpenseRepository;
import com.expenseanalyzer.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("emailNotifications", user.getEmailNotifications());
        profile.put("showSensitiveInfo", user.getShowSensitiveInfo());
        
        return profile;
    }
    
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user: {}", userId);
    }
    
    @Transactional
    public Map<String, Object> updatePrivacySettings(Long userId, Map<String, Boolean> settings) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (settings.containsKey("emailNotifications")) {
            user.setEmailNotifications(settings.get("emailNotifications"));
        }
        
        if (settings.containsKey("showSensitiveInfo")) {
            user.setShowSensitiveInfo(settings.get("showSensitiveInfo"));
        }
        
        user = userRepository.save(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("emailNotifications", user.getEmailNotifications());
        result.put("showSensitiveInfo", user.getShowSensitiveInfo());
        result.put("message", "Privacy settings updated successfully");
        
        logger.info("Privacy settings updated for user: {}", userId);
        
        return result;
    }
    
    public byte[] exportUserDataToCsv(Long userId) {
        logger.info("Exporting data to CSV for user: {}", userId);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos, true, StandardCharsets.UTF_8)) {
            
            // CSV Header
            writer.println("Type,Amount,Category,Description,Date");
            
            // Export Expenses
            var expenses = expenseRepository.findByUserIdOrderByDateDesc(userId);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (var expense : expenses) {
                writer.printf("Expense,%.2f,%s,\"%s\",%s%n",
                    expense.getAmount().doubleValue(),
                    escapeCsv(expense.getCategory()),
                    escapeCsv(expense.getDescription() != null ? expense.getDescription() : ""),
                    expense.getDate().format(dateFormatter)
                );
            }
            
            // Export Income
            var incomes = incomeRepository.findByUserIdOrderByDateDesc(userId);
            
            for (var income : incomes) {
                writer.printf("Income,%.2f,%s,\"%s\",%s%n",
                    income.getAmount().doubleValue(),
                    escapeCsv(income.getType()),
                    escapeCsv(income.getDescription() != null ? income.getDescription() : ""),
                    income.getDate().format(dateFormatter)
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error exporting CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export data to CSV");
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes and handle commas
        return value.replace("\"", "\"\"");
    }
}
