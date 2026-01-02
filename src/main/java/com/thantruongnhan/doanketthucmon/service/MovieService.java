package com.thantruongnhan.doanketthucmon.service;

import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

public interface MovieService {

    List<Movie> getAllMovies();

    Movie getMovieById(Long id);

    Movie createMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    void deleteMovie(Long id);

    List<Movie> getMoviesByStatus(MovieStatus status);

    List<Movie> searchMovies(String keyword);
}
