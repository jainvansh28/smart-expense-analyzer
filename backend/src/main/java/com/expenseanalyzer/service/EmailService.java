package com.expenseanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendOtpEmail(String to, String otp) {
        // Print OTP to console as backup
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📧 EMAIL VERIFICATION OTP");
        System.out.println("=".repeat(60));
        System.out.println("To: " + to);
        System.out.println("OTP: " + otp);
        System.out.println("Valid for: 10 minutes");
        System.out.println("=".repeat(60) + "\n");
        
        // Send actual email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Smart Expense Analyzer - Email Verification");
            message.setText(
                "Hello!\n\n" +
                "Your OTP for email verification is: " + otp + "\n\n" +
                "This OTP will expire in 10 minutes.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Smart Expense Analyzer Team"
            );
            
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            System.err.println("But OTP is still valid - check console above for OTP");
        }
    }
}
