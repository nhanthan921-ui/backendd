package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.enums.ShowFormat;

@Entity
@Table(name = "show_times")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Product movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(name = "show_time", nullable = false) // ✅ FIX: thêm name để tránh conflict
    private LocalTime showTime; // 09:00, 11:30, 14:00...

    @Column(nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "show_format", nullable = false) // ✅ FIX: format -> show_format
    private ShowFormat format; // 2D, 3D

    @Column(nullable = false)
    private Double basePrice;

    private Integer totalSeats = 0;

    private Integer availableSeats = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "showTime", cascade = CascadeType.ALL)
    private List<Seat> seats;

    @OneToMany(mappedBy = "showTime", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}