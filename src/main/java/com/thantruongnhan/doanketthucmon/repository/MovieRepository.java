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

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId AND m.status = :status")
    List<Movie> findByGenresIdAndStatus(@Param("genreId") Long genreId, @Param("status") MovieStatus status);
}
