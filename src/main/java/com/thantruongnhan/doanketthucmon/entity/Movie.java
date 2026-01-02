package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String posterUrl;

    private Integer duration; // phút

    private Double rating; // 8.5

    @Enumerated(EnumType.STRING)
    private MovieStatus status; // NOW_SHOWING, COMING_SOON

    @ManyToMany
    @JoinTable(name = "movie_categories", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    private Boolean isActive = true;
}
