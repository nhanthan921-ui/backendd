package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Cinema;
import com.thantruongnhan.doanketthucmon.repository.CinemaRepository;
import com.thantruongnhan.doanketthucmon.service.CinemaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaRepository cinemaRepository;

    @Override
    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }

    @Override
    public Cinema getCinemaById(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));
    }

    @Override
    public Cinema createCinema(String name, String address) {

        if (cinemaRepository.existsByName(name)) {
            throw new RuntimeException("Cinema already exists");
        }

        Cinema cinema = new Cinema();
        cinema.setName(name);
        cinema.setAddress(address);

        return cinemaRepository.save(cinema);
    }

    @Override
    public Cinema updateCinema(Long id, String name, String address) {

        Cinema existing = getCinemaById(id);
        existing.setName(name);
        existing.setAddress(address);

        return cinemaRepository.save(existing);
    }

    @Override
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }
}
