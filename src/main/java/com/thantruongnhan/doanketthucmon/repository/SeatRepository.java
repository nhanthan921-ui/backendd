package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByRoomId(Long roomId);

    boolean existsByRoomIdAndRowSeatAndNumber(Long roomId, String rowSeat, Integer number);

    @Query("""
                SELECT s
                FROM Seat s
                JOIN s.room r
                JOIN Showtime st ON st.room.id = r.id
                WHERE st.id = :showtimeId
            """)
    List<Seat> findSeatsByShowtime(@Param("showtimeId") Long showtimeId);

}
