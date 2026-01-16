package com.thantruongnhan.doanketthucmon.payos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOSItem {

    // Ví dụ: "Vé phim Avengers - Ghế A5"
    private String name;

    // Số lượng vé (thường = 1)
    private int quantity;

    // Giá 1 vé (VND)
    private int price;
}
