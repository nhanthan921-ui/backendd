package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Genre;
import com.thantruongnhan.doanketthucmon.repository.GenreRepository;
import com.thantruongnhan.doanketthucmon.service.GenreService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Long id, Genre genre) {
        Genre existing = getGenreById(id);
        existing.setName(genre.getName());
        return genreRepository.save(existing);
    }

    @Override
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
