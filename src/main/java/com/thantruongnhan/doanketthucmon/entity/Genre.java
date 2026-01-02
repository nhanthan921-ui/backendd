package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Action, Comedy, Horror...

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Movie> movies;
}
