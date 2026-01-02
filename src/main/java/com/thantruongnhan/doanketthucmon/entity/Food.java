package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Bắp rang
    private Integer price;
    private String imageUrl;
}
