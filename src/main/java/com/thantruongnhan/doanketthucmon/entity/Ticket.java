package com.thantruongnhan.doanketthucmon.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thantruongnhan.doanketthucmon.entity.enums.TicketStatus;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tickets", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "showtime_id", "seat_id" })
})
@Getter
@Setter

public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Suất chiếu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "tickets" })
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "tickets" })
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "tickets" })
    private User user;

    // Giá tại thời điểm đặt
    private Integer price;

    // Trạng thái vé
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    // Thời gian đặt
    private LocalDateTime bookedAt;

    // Mã vé / QR
    @Column(unique = true)
    private String ticketCode;
}
