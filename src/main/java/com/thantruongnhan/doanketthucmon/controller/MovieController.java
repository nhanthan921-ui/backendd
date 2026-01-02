package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;
import com.thantruongnhan.doanketthucmon.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/movies")
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Movie createMovie(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("duration") Integer duration,
            @RequestParam("rating") Double rating,
            @RequestParam("status") MovieStatus status,
            @RequestParam("genreIds") List<Long> genreIds,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        return movieService.createMovie(
                title, description, duration, rating, status, genreIds, poster);
    }

    // 👉 UPDATE MOVIE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Movie updateMovie(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("duration") Integer duration,
            @RequestParam("rating") Double rating,
            @RequestParam("status") MovieStatus status,
            @RequestParam("genreIds") List<Long> genreIds,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        return movieService.updateMovie(
                id, title, description, duration, rating, status, genreIds, poster);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> getMoviesByStatus(@PathVariable MovieStatus status) {
        return movieService.getMoviesByStatus(status);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> searchMovies(@RequestParam String keyword) {
        return movieService.searchMovies(keyword);
    }
}
