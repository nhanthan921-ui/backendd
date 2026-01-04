package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Cinema;
import com.thantruongnhan.doanketthucmon.entity.Room;
import com.thantruongnhan.doanketthucmon.repository.CinemaRepository;
import com.thantruongnhan.doanketthucmon.repository.RoomRepository;
import com.thantruongnhan.doanketthucmon.service.RoomService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public List<Room> getRoomsByCinema(Long cinemaId) {
        return roomRepository.findByCinemaId(cinemaId);
    }

    @Override
    public Room createRoom(Long cinemaId, String name) {

        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));

        if (roomRepository.existsByCinemaIdAndName(cinemaId, name)) {
            throw new RuntimeException("Room name already exists in this cinema");
        }

        Room room = new Room();
        room.setName(name);
        room.setCinema(cinema);

        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, String name) {

        Room existing = getRoomById(id);
        existing.setName(name);

        return roomRepository.save(existing);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
