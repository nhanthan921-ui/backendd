package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cinemas")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // CGV Vincom
    private String address; // Vincom Center, Q1
}
