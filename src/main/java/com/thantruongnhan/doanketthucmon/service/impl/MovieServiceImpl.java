package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;
import com.thantruongnhan.doanketthucmon.repository.MovieRepository;
import com.thantruongnhan.doanketthucmon.service.MovieService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    @Override
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        Movie existing = getMovieById(id);

        existing.setTitle(movie.getTitle());
        existing.setDescription(movie.getDescription());
        existing.setPosterUrl(movie.getPosterUrl());
        existing.setDuration(movie.getDuration());
        existing.setRating(movie.getRating());
        existing.setStatus(movie.getStatus());
        existing.setGenres(movie.getGenres());

        return movieRepository.save(existing);
    }

    @Override
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> getMoviesByStatus(MovieStatus status) {
        return movieRepository.findByStatus(status);
    }

    @Override
    public List<Movie> searchMovies(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
