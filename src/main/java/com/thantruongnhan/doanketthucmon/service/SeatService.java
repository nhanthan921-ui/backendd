package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;

import java.util.List;

public interface SeatService {

    List<Seat> getAllSeats();

    Seat getSeatById(Long id);

    List<Seat> getSeatsByRoom(Long roomId);

    Seat createSeat(Long roomId, String rowSeat, Integer number, SeatType type);

    Seat updateSeat(Long id, String rowSeat, Integer number, SeatType type);

    void deleteSeat(Long id);
}
