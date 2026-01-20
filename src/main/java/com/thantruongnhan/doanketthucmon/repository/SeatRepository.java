package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRoomId(Long roomId);

    boolean existsByRoomIdAndRowSeatAndNumber(Long roomId, String rowSeat, Integer number);

    @Query("SELECT s FROM Seat s " +
            "WHERE s.room.id = (SELECT st.room.id FROM Showtime st WHERE st.id = :showtimeId)")
    List<Seat> findByShowtime(@Param("showtimeId") Long showtimeId);
}
