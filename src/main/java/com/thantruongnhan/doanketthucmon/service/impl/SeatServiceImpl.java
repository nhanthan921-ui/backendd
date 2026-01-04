package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Room;
import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;
import com.thantruongnhan.doanketthucmon.repository.RoomRepository;
import com.thantruongnhan.doanketthucmon.repository.SeatRepository;
import com.thantruongnhan.doanketthucmon.service.SeatService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override
    public Seat getSeatById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
    }

    @Override
    public List<Seat> getSeatsByRoom(Long roomId) {
        return seatRepository.findByRoomId(roomId);
    }

    @Override
    public Seat createSeat(Long roomId, String rowSeat, Integer number, SeatType type) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // tránh trùng ghế
        if (seatRepository.existsByRoomIdAndRowSeatAndNumber(roomId, rowSeat, number)) {
            throw new RuntimeException("Seat already exists in this room");
        }

        Seat seat = new Seat();
        seat.setRoom(room);
        seat.setRowSeat(rowSeat);
        seat.setNumber(number);
        seat.setType(type);

        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(Long id, String rowSeat, Integer number, SeatType type) {

        Seat existing = getSeatById(id);
        existing.setRowSeat(rowSeat);
        existing.setNumber(number);
        existing.setType(type);

        return seatRepository.save(existing);
    }

    @Override
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }
}
