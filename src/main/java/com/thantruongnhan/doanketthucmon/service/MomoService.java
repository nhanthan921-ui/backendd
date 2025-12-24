package com.thantruongnhan.doanketthucmon.service;

import java.util.Map;

public interface MomoService {
    Map<String, Object> createPayment(String orderId, Long amount, String orderInfo) throws Exception;
}
