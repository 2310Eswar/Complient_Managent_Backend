package com.college.complaint.service;

import com.college.complaint.dto.ApiResponse;
import com.college.complaint.dto.ForgotPasswordRequest;
import com.college.complaint.dto.ResetPasswordRequest;
import com.college.complaint.dto.VerifyOtpRequest;
import com.college.complaint.entity.PasswordResetOtp;
import com.college.complaint.entity.Role;
import com.college.complaint.entity.User;
import com.college.complaint.repository.PasswordResetOtpRepository;
import com.college.complaint.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetOtpRepository otpRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Test Student", "student@college.edu", "hashedOldPass", Role.STUDENT, "CSE", "9876543210");
    }

    @Test
    void testSendOtp_Success() {
        when(userRepository.findByEmail("student@college.edu")).thenReturn(Optional.of(sampleUser));
        when(otpRepository.save(any(PasswordResetOtp.class))).thenAnswer(i -> i.getArgument(0));

        ForgotPasswordRequest request = new ForgotPasswordRequest("student@college.edu");
        ApiResponse response = passwordResetService.sendOtp(request);

        assertTrue(response.isSuccess());
        verify(emailService, times(1)).sendPasswordResetOtp(eq("student@college.edu"), anyString(), eq(10));
        verify(otpRepository, times(1)).save(any(PasswordResetOtp.class));
    }

    @Test
    void testSendOtp_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@college.edu")).thenReturn(Optional.empty());

        ForgotPasswordRequest request = new ForgotPasswordRequest("unknown@college.edu");
        Exception ex = assertThrows(RuntimeException.class, () -> passwordResetService.sendOtp(request));

        assertTrue(ex.getMessage().contains("No account found"));
        verifyNoInteractions(emailService);
    }

    @Test
    void testVerifyOtp_Success() {
        PasswordResetOtp validOtp = new PasswordResetOtp("student@college.edu", "123456", LocalDateTime.now().plusMinutes(5));
        when(otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc("student@college.edu", "123456"))
                .thenReturn(Optional.of(validOtp));

        VerifyOtpRequest request = new VerifyOtpRequest("student@college.edu", "123456");
        ApiResponse response = passwordResetService.verifyOtp(request);

        assertTrue(response.isSuccess());
    }

    @Test
    void testVerifyOtp_Expired_ThrowsException() {
        PasswordResetOtp expiredOtp = new PasswordResetOtp("student@college.edu", "123456", LocalDateTime.now().minusMinutes(1));
        when(otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc("student@college.edu", "123456"))
                .thenReturn(Optional.of(expiredOtp));

        VerifyOtpRequest request = new VerifyOtpRequest("student@college.edu", "123456");
        Exception ex = assertThrows(RuntimeException.class, () -> passwordResetService.verifyOtp(request));

        assertTrue(ex.getMessage().contains("expired"));
    }

    @Test
    void testResetPassword_Success() {
        PasswordResetOtp validOtp = new PasswordResetOtp("student@college.edu", "654321", LocalDateTime.now().plusMinutes(5));
        when(otpRepository.findTopByEmailAndOtpOrderByCreatedAtDesc("student@college.edu", "654321"))
                .thenReturn(Optional.of(validOtp));
        when(userRepository.findByEmail("student@college.edu")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode("newSecretPass")).thenReturn("encodedNewSecretPass");

        ResetPasswordRequest request = new ResetPasswordRequest("student@college.edu", "654321", "newSecretPass");
        ApiResponse response = passwordResetService.resetPassword(request);

        assertTrue(response.isSuccess());
        assertTrue(validOtp.isVerified());
        assertEquals("encodedNewSecretPass", sampleUser.getPassword());
        verify(userRepository, times(1)).save(sampleUser);
        verify(otpRepository, times(1)).save(validOtp);
    }
}
