package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Room 1, Room 2

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
