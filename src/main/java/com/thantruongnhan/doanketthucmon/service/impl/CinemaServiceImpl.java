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
    public Cinema createCinema(Cinema cinema) {

        if (cinemaRepository.existsByName(cinema.getName())) {
            throw new RuntimeException("Cinema already exists");
        }

        return cinemaRepository.save(cinema);
    }

    @Override
    public Cinema updateCinema(Long id, Cinema cinema) {

        Cinema existing = getCinemaById(id);
        existing.setName(cinema.getName());
        existing.setAddress(cinema.getAddress());

        return cinemaRepository.save(existing);
    }

    @Override
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }
}
