package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.entity.Cinema;
import com.thantruongnhan.doanketthucmon.service.CinemaService;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
@CrossOrigin
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Cinema> getAllCinemas() {
        return cinemaService.getAllCinemas();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Cinema getCinemaById(@PathVariable Long id) {
        return cinemaService.getCinemaById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cinema createCinema(
            @RequestParam String name,
            @RequestParam String address) {

        return cinemaService.createCinema(name, address);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Cinema updateCinema(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String address) {

        return cinemaService.updateCinema(id, name, address);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
    }
}
