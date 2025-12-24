package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.momo.MomoRequest;
import com.thantruongnhan.doanketthucmon.momo.OrderStore;
import com.thantruongnhan.doanketthucmon.service.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/momo")
@CrossOrigin(origins = "http://localhost:3000")
public class MomoController {

    @Autowired
    private MomoService momoService;

    // Create payment -> returns MoMo response (payUrl, qrCodeUrl, etc)
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody MomoRequest req) {
        try {
            Map<String, Object> momoResp = momoService.createPayment(req.getOrderId(), req.getAmount(),
                    req.getOrderInfo());
            return ResponseEntity.ok(momoResp);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("message", "Cannot create momo payment: " + e.getMessage());
            return ResponseEntity.status(500).body(err);
        }
    }

    // IPN from MoMo (notify)
    @PostMapping("/ipn")
    public ResponseEntity<?> ipn(@RequestBody Map<String, Object> data) {
        try {
            String orderId = String.valueOf(data.get("orderId"));
            Object rc = data.get("resultCode");
            int resultCode = rc == null ? -1 : Integer.parseInt(rc.toString());

            if (resultCode == 0) {
                OrderStore.save(orderId, "PAID");
            } else {
                OrderStore.save(orderId, "FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("OK");
    }

    // frontend will poll this to get status
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> status(@PathVariable String orderId) {
        String status = OrderStore.get(orderId);
        if (status == null)
            status = "NOT_FOUND";
        Map<String, String> resp = new HashMap<>();
        resp.put("orderId", orderId);
        resp.put("status", status);
        return ResponseEntity.ok(resp);
    }
}
