package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thantruongnhan.doanketthucmon.entity.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByShowtimeId(Long showtimeId);

    List<Ticket> findBySeatId(Long seatId);

    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId ORDER BY t.showtime.startTime DESC")
    List<Ticket> findByUserId(@Param("userId") Long userId);

    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);
}
