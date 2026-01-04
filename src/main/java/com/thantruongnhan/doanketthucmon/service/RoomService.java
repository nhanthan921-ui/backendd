package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> getAllRooms();

    Room getRoomById(Long id);

    List<Room> getRoomsByCinema(Long cinemaId);

    Room createRoom(Long cinemaId, String name);

    Room updateRoom(Long id, String name);

    void deleteRoom(Long id);
}
