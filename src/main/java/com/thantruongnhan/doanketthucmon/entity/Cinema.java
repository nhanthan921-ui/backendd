package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // CGV Vincom
    private String address; // Vincom Center, Q1
}
