package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.service.PayOSWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final PayOSWebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> payload) {

        System.out.println(">>> Received PayOS webhook: " + payload);

        try {
            // 1. Lấy signature
            String signature = (String) payload.get("signature");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            if (data == null) {
                return ResponseEntity.badRequest().body("Missing data");
            }

            // 2. Verify signature
            if (signature != null && !webhookService.verifySignature(data, signature)) {
                return ResponseEntity.status(401).body("Invalid signature");
            }

            // 3. Parse dữ liệu
            Long orderCode = Long.valueOf(data.get("orderCode").toString());
            String status = (String) data.get("status"); // PAID | CANCELLED | PENDING

            System.out.println(">>> orderCode: " + orderCode);
            System.out.println(">>> status: " + status);

            // 4. Xử lý theo trạng thái
            switch (status) {
                case "PAID" -> {
                    System.out.println("✅ Payment SUCCESS for ticket orderCode: " + orderCode);
                    // TODO:
                    // - Update Ticket status = PAID
                    // - Generate ticketCode / QR
                }
                case "CANCELLED" -> {
                    System.out.println("❌ Payment CANCELLED for ticket orderCode: " + orderCode);
                    // TODO:
                    // - Release seat
                    // - Update Ticket status = CANCELLED
                }
                default -> {
                    System.out.println("ℹ️ Payment status: " + status);
                }
            }

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Webhook processing failed");
        }
    }
}
