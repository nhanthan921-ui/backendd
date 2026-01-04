package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Showtime showtime;

    @ManyToOne
    private Seat seat;

    private Integer price;
}
