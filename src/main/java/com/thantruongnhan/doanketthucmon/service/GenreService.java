package com.thantruongnhan.doanketthucmon.service;

import java.util.List;

import com.thantruongnhan.doanketthucmon.entity.Genre;

public interface GenreService {

    List<Genre> getAllGenres();

    Genre getGenreById(Long id);

    Genre createGenre(Genre genre);

    Genre updateGenre(Long id, Genre genre);

    void deleteGenre(Long id);
}
