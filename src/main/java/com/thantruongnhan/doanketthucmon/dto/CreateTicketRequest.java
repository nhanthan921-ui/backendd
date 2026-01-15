package com.thantruongnhan.doanketthucmon.dto;

import lombok.Data;

@Data
public class CreateTicketRequest {
    private Long showtimeId;
    private Long seatId;
    private Long userId;
}
