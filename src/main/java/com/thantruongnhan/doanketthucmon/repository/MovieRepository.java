package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByStatus(MovieStatus status);

    List<Movie> findByTitleContainingIgnoreCase(String title);
}
