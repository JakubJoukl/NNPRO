package com.example.nnprorocnikovyprojekt.repositories;

import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Integer> {
    @Query("SELECT vc FROM VerificationCode vc WHERE vc.verificationCode = :verificationCode AND vc.valid = true and vc.user = :user")
    List<VerificationCode> findValidVerificationCodes(@Param("user") User user, @Param("verificationCode") String verificationCode);
}
