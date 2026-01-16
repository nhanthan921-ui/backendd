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

        System.out.println(">>> Received request:");
        System.out.println("orderCode: " + req.getOrderCode());
        System.out.println("amount: " + req.getAmount());
        System.out.println("description: " + req.getDescription());
        System.out.println("returnUrl: " + req.getReturnUrl());
        System.out.println("cancelUrl: " + req.getCancelUrl());

        // Validate
        if (req.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount"));
        }

        if (req.getOrderCode() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid orderCode"));
        }

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
            System.err.println(">>> Controller Error:");
            e.printStackTrace();

            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage(),
                    "details", e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
        }
    }
}
