package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;
import com.thantruongnhan.doanketthucmon.repository.GenreRepository;
import com.thantruongnhan.doanketthucmon.repository.MovieRepository;
import com.thantruongnhan.doanketthucmon.service.MovieService;
import com.thantruongnhan.doanketthucmon.service.StorageService;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private StorageService storageService;

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
    public Movie createMovie(
            String title,
            String description,
            Integer duration,
            Double rating,
            MovieStatus status,
            List<Long> genreIds,
            MultipartFile poster) {

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setStatus(status);

        // set genres
        movie.setGenres(new HashSet<>(genreRepository.findAllById(genreIds)));

        if (poster != null && !poster.isEmpty()) {
            String posterUrl = storageService.saveImage(poster);
            movie.setPosterUrl(posterUrl);
        }

        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(
            Long id,
            String title,
            String description,
            Integer duration,
            Double rating,
            MovieStatus status,
            List<Long> genreIds,
            MultipartFile poster) {

        Movie existing = getMovieById(id);
        existing.setTitle(title);
        existing.setDescription(description);
        existing.setDuration(duration);
        existing.setRating(rating);
        existing.setStatus(status);
        existing.setGenres(new HashSet<>(genreRepository.findAllById(genreIds)));

        if (poster != null && !poster.isEmpty()) {
            String posterUrl = storageService.saveImage(poster);
            existing.setPosterUrl(posterUrl);
        }

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

    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findByGenresId(genreId);
    }

    // Get movies by cinema (through showtimes)
    public List<Movie> getMoviesByCinema(Long cinemaId) {
        return movieRepository.findByCinemaId(cinemaId);
    }

    // Filter với nhiều điều kiện
    public List<Movie> getMoviesFiltered(Long genreId, Long cinemaId, MovieStatus status) {
        if (genreId != null && cinemaId != null && status != null) {
            return movieRepository.findByGenresIdAndCinemaIdAndStatus(genreId, cinemaId, status);
        } else if (genreId != null && status != null) {
            return movieRepository.findByGenresIdAndStatus(genreId, status);
        } else if (cinemaId != null && status != null) {
            return movieRepository.findByCinemaIdAndStatus(cinemaId, status);
        } else if (genreId != null) {
            return movieRepository.findByGenresId(genreId);
        } else if (cinemaId != null) {
            return movieRepository.findByCinemaId(cinemaId);
        } else if (status != null) {
            return movieRepository.findByStatus(status);
        } else {
            return movieRepository.findAll();
        }
    }
}
