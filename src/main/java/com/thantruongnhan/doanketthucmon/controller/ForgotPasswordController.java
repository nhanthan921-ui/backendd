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
        try {
            String email = body.get("email");

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email không được để trống"));
            }

            passwordResetService.sendOtp(email);

            return ResponseEntity.ok(Map.of("message", "OTP đã được gửi về email"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        passwordResetService.resetPassword(
                body.get("otp"),
                body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }
}
