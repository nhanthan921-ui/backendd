package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.payos.CreatePaymentRequest;
import com.thantruongnhan.doanketthucmon.payos.PayOSClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOSController {

    private final PayOSClient payOSClient;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest req) {

        // ✅ Validate đầy đủ
        if (req.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount"));
        }

        if (req.getOrderCode() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid orderCode"));
        }

        // ✅ KIỂM TRA returnUrl và cancelUrl
        if (req.getReturnUrl() == null || req.getReturnUrl().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "returnUrl is required"));
        }

        if (req.getCancelUrl() == null || req.getCancelUrl().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "cancelUrl is required"));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderCode", req.getOrderCode());
        payload.put("amount", req.getAmount());
        payload.put("description", req.getDescription());
        payload.put("returnUrl", req.getReturnUrl());
        payload.put("cancelUrl", req.getCancelUrl());

        if (req.getItems() != null && !req.getItems().isEmpty()) {
            payload.put("items", req.getItems());
        }

        try {
            Map<String, Object> response = payOSClient.createPaymentLink(payload);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
