package com.thantruongnhan.doanketthucmon.payos;

import lombok.Data;
import java.util.List;

@Data
public class CreatePaymentRequest {

    // Mã đơn PayOS
    private long orderCode;

    // Tổng tiền (PayOS lấy tiền từ đây)
    private int amount;

    // Mô tả hiển thị
    private String description;

    private String returnUrl;
    private String cancelUrl;

    // Danh sách vé (chỉ để hiển thị)
    private List<Item> items;

    @Data
    public static class Item {

        // Ví dụ: "Vé phim - Ghế A5"
        private String name;

        // Thường = 1
        private int quantity;

        // Giá 1 vé
        private int price;

        // RẤT QUAN TRỌNG – map lại Ticket
        private Long ticketId;
    }
}
