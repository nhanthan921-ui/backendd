package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;
import com.thantruongnhan.doanketthucmon.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieService.createMovie(movie);
    }

    @PutMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        return movieService.updateMovie(id, movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @GetMapping("/status/{status}")
    public List<Movie> getMoviesByStatus(@PathVariable MovieStatus status) {
        return movieService.getMoviesByStatus(status);
    }

    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam String keyword) {
        return movieService.searchMovies(keyword);
    }
}
