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
@CrossOrigin(origins = "http://localhost:8081")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
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
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Room updateRoom(
            @PathVariable Long id,
            @RequestBody Room room) {

        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }
}
