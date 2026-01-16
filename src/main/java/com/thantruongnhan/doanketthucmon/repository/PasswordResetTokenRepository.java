package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.PasswordResetToken;
import com.thantruongnhan.doanketthucmon.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByOtp(String otp);

    void deleteByUser(User user);
}
