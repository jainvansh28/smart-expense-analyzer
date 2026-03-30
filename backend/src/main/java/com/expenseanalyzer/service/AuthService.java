package com.expenseanalyzer.service;

import com.expenseanalyzer.dto.AuthResponse;
import com.expenseanalyzer.dto.LoginRequest;
import com.expenseanalyzer.dto.SignupRequest;
import com.expenseanalyzer.model.OtpVerification;
import com.expenseanalyzer.model.User;
import com.expenseanalyzer.repository.OtpRepository;
import com.expenseanalyzer.repository.UserRepository;
import com.expenseanalyzer.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public String sendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        
        String otp = generateOtp();
        
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otpVerification.setVerified(false);
        
        otpRepository.save(otpVerification);
        
        // Temporarily disable email sending for testing
        try {
            emailService.sendOtpEmail(email, otp);
        } catch (Exception e) {
            System.out.println("Email sending failed, but continuing. OTP: " + otp);
        }
        
        // Print OTP to console for testing
        System.out.println("===========================================");
        System.out.println("OTP for " + email + " is: " + otp);
        System.out.println("===========================================");
        
        return "OTP sent successfully (check console for OTP)";
    }
    
    @Transactional
    public String verifyOtp(String email, String otp) {
        OtpVerification otpVerification = otpRepository.findByEmailAndOtpAndVerifiedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        otpVerification.setVerified(true);
        otpRepository.save(otpVerification);
        
        return "OTP verified successfully";
    }
    
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), 
            user.getCreatedAt(), user.getEmailNotifications(), user.getShowSensitiveInfo());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(),
            user.getCreatedAt(), user.getEmailNotifications(), user.getShowSensitiveInfo());
    }
    
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
