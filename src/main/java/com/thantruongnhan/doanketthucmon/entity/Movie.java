package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String description;

    private String posterUrl;

    private Integer duration; // phút

    private Double rating;

    @Enumerated(EnumType.STRING)
    private MovieStatus status; // NOW_SHOWING, COMING_SOON

    @ManyToMany
    @JoinTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @JsonManagedReference
    private Set<Genre> genres;
}
