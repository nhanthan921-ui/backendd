package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Cinema;

import java.util.List;

public interface CinemaService {

    List<Cinema> getAllCinemas();

    Cinema getCinemaById(Long id);

    Cinema createCinema(String name, String address);

    Cinema updateCinema(Long id, String name, String address);

    void deleteCinema(Long id);
}
