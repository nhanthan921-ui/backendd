package com.thantruongnhan.doanketthucmon.momo;

import lombok.Data;

@Data
public class MomoRequest {
    private String orderId;
    private Long amount;
    private String orderInfo;
}
