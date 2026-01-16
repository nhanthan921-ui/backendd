package com.thantruongnhan.doanketthucmon.service;

public interface PasswordResetService {

    void sendOtp(String email);

    void resetPassword(String otp, String newPassword);
}
