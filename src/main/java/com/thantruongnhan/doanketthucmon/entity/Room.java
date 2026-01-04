package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Room 1, Room 2

    @ManyToOne
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;
}
