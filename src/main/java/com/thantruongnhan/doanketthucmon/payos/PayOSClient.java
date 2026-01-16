package com.thantruongnhan.doanketthucmon.payos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class PayOSClient {

    private final String clientId;
    private final String apiKey;
    private final String checksumKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://api-merchant.payos.vn/v2/payment-requests";

    public PayOSClient(
            @Value("${payos.clientId}") String clientId,
            @Value("${payos.apiKey}") String apiKey,
            @Value("${payos.checksumKey}") String checksumKey) {
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.checksumKey = checksumKey;

        System.out.println(">>> PayOS Config:");
        System.out.println("clientId: " + clientId);
        System.out.println("apiKey: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "null"));
        System.out.println("checksumKey length: " + (checksumKey != null ? checksumKey.length() : 0));
    }

    public Map<String, Object> createPaymentLink(Map<String, Object> payload) {
        try {
            // ✅ Tạo signature TRƯỚC
            String signature = createSignature(payload);
            payload.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            System.out.println(">>> FULL PAYLOAD:");
            System.out.println(new ObjectMapper().writeValueAsString(payload));

            ResponseEntity<Map> res = restTemplate.postForEntity(BASE_URL, entity, Map.class);

            System.out.println(">>> PayOS SUCCESS Response:");
            System.out.println(new ObjectMapper().writeValueAsString(res.getBody()));

            return res.getBody();

        } catch (HttpStatusCodeException ex) {
            System.err.println(">>> PayOS API Error:");
            System.err.println("Status: " + ex.getStatusCode());
            System.err.println("Response: " + ex.getResponseBodyAsString());
            throw new RuntimeException("PayOS Error: " + ex.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println(">>> General Error:");
            e.printStackTrace();
            throw new RuntimeException("Error creating payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getPaymentStatus(String orderCode) {
        try {
            String url = BASE_URL + "/" + orderCode;

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            System.out.println(">>> Checking payment status for order: " + orderCode);

            ResponseEntity<Map> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class);

            return res.getBody();

        } catch (HttpStatusCodeException ex) {
            System.err.println(">>> PayOS Check Status Error: " + ex.getResponseBodyAsString());
            throw new RuntimeException(ex.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error checking payment status", e);
        }
    }

    /**
     * ✅ Tạo signature theo đúng format PayOS
     * Format:
     * amount={amount}&cancelUrl={cancelUrl}&description={description}&orderCode={orderCode}&returnUrl={returnUrl}
     */
    private String createSignature(Map<String, Object> payload) throws Exception {
        try {
            // Lấy các giá trị
            Object amountObj = payload.get("amount");
            String cancelUrl = (String) payload.get("cancelUrl");
            String description = (String) payload.get("description");
            Object orderCodeObj = payload.get("orderCode");
            String returnUrl = (String) payload.get("returnUrl");

            // Convert sang đúng kiểu
            int amount = (amountObj instanceof Integer) ? (Integer) amountObj : Integer.parseInt(amountObj.toString());

            long orderCode = (orderCodeObj instanceof Long) ? (Long) orderCodeObj
                    : (orderCodeObj instanceof Integer) ? ((Integer) orderCodeObj).longValue()
                            : Long.parseLong(orderCodeObj.toString());

            // ✅ Tạo chuỗi data theo format CHÍNH XÁC của PayOS
            // LƯU Ý: Các tham số phải theo thứ tự alphabet: amount, cancelUrl, description,
            // orderCode, returnUrl
            String data = String.format(
                    "amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                    amount,
                    cancelUrl,
                    description,
                    orderCode,
                    returnUrl);

            System.out.println(">>> Data to sign:");
            System.out.println(data);

            // Tính HMAC SHA256
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(
                    checksumKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            hmac.init(secret);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert sang hex string
            String signature = bytesToHex(hash);

            System.out.println(">>> Generated signature:");
            System.out.println(signature);

            return signature;

        } catch (Exception e) {
            System.err.println(">>> Error creating signature:");
            e.printStackTrace();
            throw new Exception("Failed to create signature: " + e.getMessage(), e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}