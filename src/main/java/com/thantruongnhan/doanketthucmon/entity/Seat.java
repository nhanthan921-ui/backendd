package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.thantruongnhan.doanketthucmon.entity.enums.SeatStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_time_id", nullable = false)
    private ShowTime showTime;

    @Column(name = "row_letter", nullable = false, length = 1) // ✅ FIX: row -> row_letter
    private String row; // A, B, C, D, E, F, G, H

    @Column(name = "seat_number", nullable = false) // ✅ FIX: number -> seat_number
    private Integer number; // 1, 2, 3, 4, 5, 6, 7, 8, 9

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType type; // NORMAL, VIP

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", nullable = false)
    private SeatStatus status; // AVAILABLE, BOOKED, SELECTED, RESERVED

    @Column(nullable = false)
    private Double price; // Giá ghế (85.000đ cho thường, 120.000đ cho VIP)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reservedBy; // Người đang giữ ghế tạm thời

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil; // Thời gian hết hạn giữ ghế

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method để tạo tên ghế
    public String getSeatName() {
        return row + number;
    }
}