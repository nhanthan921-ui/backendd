package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.entity.Room;
import com.thantruongnhan.doanketthucmon.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/rooms")
@RequiredArgsConstructor
@CrossOrigin
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/cinema/{cinemaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Room> getRoomsByCinema(@PathVariable Long cinemaId) {
        return roomService.getRoomsByCinema(cinemaId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Room createRoom(
            @RequestParam Long cinemaId,
            @RequestParam String name) {

        return roomService.createRoom(cinemaId, name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Room updateRoom(
            @PathVariable Long id,
            @RequestParam String name) {

        return roomService.updateRoom(id, name);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
