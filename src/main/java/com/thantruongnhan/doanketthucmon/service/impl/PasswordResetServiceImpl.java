package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.thantruongnhan.doanketthucmon.entity.PasswordResetToken;
import com.thantruongnhan.doanketthucmon.entity.User;
import com.thantruongnhan.doanketthucmon.repository.PasswordResetTokenRepository;
import com.thantruongnhan.doanketthucmon.repository.UserRepository;
import com.thantruongnhan.doanketthucmon.service.EmailService;
import com.thantruongnhan.doanketthucmon.service.PasswordResetService;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendOtp(String email) {
        log.info("📩 Nhận yêu cầu gửi OTP cho: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        tokenRepository.findByUser(user)
                .ifPresent(tokenRepository::delete);

        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetToken token = PasswordResetToken.builder()
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();

        tokenRepository.save(token);

        log.info("✅ OTP đã tạo: {}", otp);

        // 🔥 QUAN TRỌNG
        emailService.sendOtpEmail(email, otp);

        log.info("📧 Email OTP đã gửi thành công");
    }

    @Override
    public void resetPassword(String otp, String newPassword) {

        PasswordResetToken token = tokenRepository.findByOtp(otp)
                .orElseThrow(() -> new IllegalArgumentException("OTP không đúng"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("OTP đã hết hạn");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        tokenRepository.delete(token);
    }
}
