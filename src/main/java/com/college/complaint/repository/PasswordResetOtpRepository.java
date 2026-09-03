package com.college.complaint.repository;

import com.college.complaint.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findTopByEmailOrderByCreatedAtDesc(String email);
    Optional<PasswordResetOtp> findTopByEmailAndOtpOrderByCreatedAtDesc(String email, String otp);
    void deleteByEmail(String email);
}
