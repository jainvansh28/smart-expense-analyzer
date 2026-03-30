package com.expenseanalyzer.controller;

import com.expenseanalyzer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Map<String, Object> profile = userService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            userService.changePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/privacy-settings")
    public ResponseEntity<?> updatePrivacySettings(@RequestBody Map<String, Boolean> settings, Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            Map<String, Object> updatedSettings = userService.updatePrivacySettings(userId, settings);
            return ResponseEntity.ok(updatedSettings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportDataToCsv(Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            byte[] csvData = userService.exportUserDataToCsv(userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "expense_data.csv");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
