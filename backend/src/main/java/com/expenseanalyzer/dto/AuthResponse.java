package com.expenseanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private Boolean emailNotifications;
    private Boolean showSensitiveInfo;
    
    public AuthResponse(String token, Long id, String name, String email, LocalDateTime createdAt, Boolean emailNotifications, Boolean showSensitiveInfo) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.emailNotifications = emailNotifications;
        this.showSensitiveInfo = showSensitiveInfo;
    }
}
