package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thantruongnhan.doanketthucmon.entity.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByShowtimeId(Long showtimeId);

    List<Ticket> findBySeatId(Long seatId);

    @Query("SELECT DISTINCT t FROM Ticket t " +
            "LEFT JOIN FETCH t.showtime s " +
            "LEFT JOIN FETCH s.movie m " +
            "LEFT JOIN FETCH s.room r " +
            "LEFT JOIN FETCH r.cinema c " +
            "LEFT JOIN FETCH t.seat se " +
            "LEFT JOIN FETCH t.user u " +
            "WHERE t.user.id = :userId " +
            "ORDER BY s.startTime DESC")
    List<Ticket> findByUserId(@Param("userId") Long userId);

    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);
}
