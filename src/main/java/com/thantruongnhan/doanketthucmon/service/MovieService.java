package com.thantruongnhan.doanketthucmon.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

public interface MovieService {

    List<Movie> getAllMovies();

    Movie getMovieById(Long id);

    Movie createMovie(
            String title, String description, Integer duration, Double rating, MovieStatus status,
            List<Long> genreIds, MultipartFile poster);

    Movie updateMovie(
            Long id, String title, String description, Integer duration, Double rating, MovieStatus status,
            List<Long> genreIds, MultipartFile poster);

    void deleteMovie(Long id);

    List<Movie> getMoviesByStatus(MovieStatus status);

    List<Movie> searchMovies(String keyword);
}
