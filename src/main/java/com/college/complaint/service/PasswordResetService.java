package com.college.complaint.service;

import com.college.complaint.dto.ApiResponse;
import com.college.complaint.dto.ForgotPasswordRequest;
import com.college.complaint.dto.ResetPasswordRequest;
import com.college.complaint.dto.VerifyOtpRequest;
import com.college.complaint.entity.PasswordResetOtp;
import com.college.complaint.entity.User;
import com.college.complaint.repository.PasswordResetOtpRepository;
import com.college.complaint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private static final int OTP_EXPIRY_MINUTES = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse sendOtp(ForgotPasswordRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("No account found with email: " + cleanEmail));

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated. Please contact campus administration.");
        }

        // Generate 6-digit random code
        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        PasswordResetOtp otpEntity = new PasswordResetOtp(cleanEmail, otp, expiry);
        otpRepository.save(otpEntity);

        // Send Email
        emailService.sendPasswordResetOtp(cleanEmail, otp, OTP_EXPIRY_MINUTES);

        return new ApiResponse(true, "A 6-digit verification code has been sent to " + cleanEmail);
    }

    @Transactional(readOnly = true)
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        String otp = request.getOtp().trim();

        PasswordResetOtp otpRecord = otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc(cleanEmail, otp)
                .orElseThrow(() -> new RuntimeException("Invalid verification code. Please check and try again."));

        if (otpRecord.isExpired()) {
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }

        if (otpRecord.isVerified()) {
            throw new RuntimeException("This verification code has already been used. Please request a new one.");
        }

        return new ApiResponse(true, "Verification code is valid.");
    }

    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        String otp = request.getOtp().trim();

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long.");
        }

        PasswordResetOtp otpRecord = otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc(cleanEmail, otp)
                .orElseThrow(() -> new RuntimeException("Invalid verification code. Please check and try again."));

        if (otpRecord.isExpired()) {
            throw new RuntimeException("Verification code has expired. Please request a new one.");
        }

        if (otpRecord.isVerified()) {
            throw new RuntimeException("This verification code has already been used. Please request a new one.");
        }

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("User account not found."));

        // Update password with BCrypt hash
        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        userRepository.save(user);

        // Mark OTP as used
        otpRecord.setVerified(true);
        otpRepository.save(otpRecord);

        return new ApiResponse(true, "Password has been successfully updated! You can now log in with your new password.");
    }
}
