package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.momo.MomoSignature;
import com.thantruongnhan.doanketthucmon.momo.OrderStore;
import com.thantruongnhan.doanketthucmon.service.MomoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MomoServiceImpl implements MomoService {

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirectUrl}")
    private String redirectUrl;

    @Value("${momo.ipnUrl}")
    private String ipnUrl;

    @Override
    public Map<String, Object> createPayment(String orderId, Long amount, String orderInfo) throws Exception {

        // ❗❗ KHÔNG tạo orderId mới nữa
        String momoOrderId = orderId;

        // requestId unique
        String requestId = UUID.randomUUID().toString();
        String amountStr = amount.toString();

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amountStr +
                "&extraData=" +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + momoOrderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=captureWallet";

        String signature = MomoSignature.sign(rawSignature, secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey", accessKey);
        body.put("requestId", requestId);
        body.put("amount", amountStr);
        body.put("orderId", momoOrderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", redirectUrl);
        body.put("ipnUrl", ipnUrl);
        body.put("requestType", "captureWallet");
        body.put("extraData", "");
        body.put("signature", signature);

        // 🔥 Lưu PENDING theo đúng orderId MoMo dùng
        OrderStore.save(momoOrderId, "PENDING");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
        Map<String, Object> respBody = response.getBody();
        return respBody;
    }
}
