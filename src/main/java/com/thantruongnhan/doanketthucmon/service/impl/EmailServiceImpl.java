package com.thantruongnhan.doanketthucmon.service.impl;

import com.resend.*;
import com.resend.services.emails.model.CreateEmailOptions;
import com.thantruongnhan.doanketthucmon.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            Resend resend = new Resend(resendApiKey);

            CreateEmailOptions email = CreateEmailOptions.builder()
                    .from("Nhan App <onboarding@resend.dev>")
                    .to(to)
                    .subject("Mã OTP đặt lại mật khẩu")
                    .html("""
                            <h2>🔐 Đặt lại mật khẩu</h2>
                            <p>Mã OTP của bạn là:</p>
                            <h1>%s</h1>
                            <p>Mã có hiệu lực trong <b>5 phút</b>.</p>
                            """.formatted(otp))
                    .build();

            resend.emails().send(email);

            log.info("📧 Gửi OTP thành công tới {}", to);

        } catch (Exception e) {
            log.error("❌ Gửi email OTP thất bại", e);
            throw new RuntimeException("Không thể gửi email OTP");
        }
    }
}
