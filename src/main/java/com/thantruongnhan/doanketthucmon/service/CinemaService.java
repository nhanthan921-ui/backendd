package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Cinema;

import java.util.List;

public interface CinemaService {
    List<Cinema> getAllCinemas();

    Cinema getCinemaById(Long id);

    Cinema createCinema(Cinema cinema);

    Cinema updateCinema(Long id, Cinema cinema);

    void deleteCinema(Long id);
}
