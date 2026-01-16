package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.service.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
@CrossOrigin
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        passwordResetService.sendOtp(body.get("email"));
        return ResponseEntity.ok(Map.of("message", "OTP đã được gửi về email"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        passwordResetService.resetPassword(
                body.get("otp"),
                body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }
}
