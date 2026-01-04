package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;
import com.thantruongnhan.doanketthucmon.service.SeatService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/seats")
@RequiredArgsConstructor
@CrossOrigin
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Seat getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Seat> getSeatsByRoom(@PathVariable Long roomId) {
        return seatService.getSeatsByRoom(roomId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Seat createSeat(
            @RequestParam Long roomId,
            @RequestParam String rowSeat,
            @RequestParam Integer number,
            @RequestParam SeatType type) {

        return seatService.createSeat(roomId, rowSeat, number, type);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Seat updateSeat(
            @PathVariable Long id,
            @RequestParam String rowSeat,
            @RequestParam Integer number,
            @RequestParam SeatType type) {

        return seatService.updateSeat(id, rowSeat, number, type);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }
}
