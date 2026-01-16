package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thantruongnhan.doanketthucmon.entity.Movie;
import com.thantruongnhan.doanketthucmon.entity.enums.MovieStatus;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByStatus(MovieStatus status);

    List<Movie> findByTitleContainingIgnoreCase(String title);

    List<Movie> findByGenresId(Long genreId);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.showtimes s WHERE s.cinema.id = :cinemaId")
    List<Movie> findByCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g JOIN m.showtimes s WHERE g.id = :genreId AND s.cinema.id = :cinemaId AND m.status = :status")
    List<Movie> findByGenresIdAndCinemaIdAndStatus(
            @Param("genreId") Long genreId,
            @Param("cinemaId") Long cinemaId,
            @Param("status") MovieStatus status);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId AND m.status = :status")
    List<Movie> findByGenresIdAndStatus(@Param("genreId") Long genreId, @Param("status") MovieStatus status);

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.showtimes s WHERE s.cinema.id = :cinemaId AND m.status = :status")
    List<Movie> findByCinemaIdAndStatus(@Param("cinemaId") Long cinemaId, @Param("status") MovieStatus status);
}
