package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;
import com.thantruongnhan.doanketthucmon.service.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/movies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Movie createMovie(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("duration") Integer duration,
            @RequestParam("rating") Double rating,
            @RequestParam("status") MovieStatus status,
            @RequestParam("genreIds") List<Long> genreIds,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {

        System.out.println("poster null? " + (poster == null));
        System.out.println("poster empty? " + (poster != null && poster.isEmpty()));
        System.out.println("poster name: " + (poster != null ? poster.getOriginalFilename() : "null"));

        return movieService.createMovie(
                title, description, duration, rating, status, genreIds, poster);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    // Filter movies theo genre
    @GetMapping("/genre/{genreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> getMoviesByGenre(@PathVariable Long genreId) {
        return movieService.getMoviesByGenre(genreId);
    }

    // Filter movies theo cinema (qua showtimes)
    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> getMoviesByCinema(@PathVariable Long cinemaId) {
        return movieService.getMoviesByCinema(cinemaId);
    }

    // Filter movies theo cả genre VÀ cinema
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Movie> getMoviesFiltered(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) MovieStatus status) {
        return movieService.getMoviesFiltered(genreId, cinemaId, status);
    }
}
