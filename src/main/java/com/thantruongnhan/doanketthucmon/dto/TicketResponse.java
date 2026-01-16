package com.thantruongnhan.doanketthucmon.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketResponse {
    private Long id;
    private Long showtimeId;
    private Long seatId;
    private Long userId;
    private Integer price;
    private String status;
    private LocalDateTime bookedAt;
    private String ticketCode;
}
