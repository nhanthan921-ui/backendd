package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRoomId(Long roomId);

    boolean existsByRoomIdAndRowSeatAndNumber(Long roomId, String rowSeat, Integer number);
}
