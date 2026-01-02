package com.thantruongnhan.doanketthucmon.entity;

import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowSeat; // A, B, C
    private Integer number; // 1,2,3

    @Enumerated(EnumType.STRING)
    private SeatType type; // NORMAL, VIP

    @ManyToOne
    private Room room;
}
