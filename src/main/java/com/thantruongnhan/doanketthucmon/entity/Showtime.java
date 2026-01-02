package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private String format; // 2D, 3D, IMAX
    private Integer price;

    @ManyToOne
    private Movie movie;

    @ManyToOne
    private Room room;
}
