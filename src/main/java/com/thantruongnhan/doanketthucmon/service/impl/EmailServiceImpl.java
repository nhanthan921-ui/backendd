package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Mã OTP đặt lại mật khẩu");
            helper.setText(
                    "<h2>Mã OTP của bạn</h2>" +
                            "<p>Mã OTP: <b>" + otp + "</b></p>" +
                            "<p>Mã này có hiệu lực trong 5 phút.</p>",
                    true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage(), e);
        }
    }
}
