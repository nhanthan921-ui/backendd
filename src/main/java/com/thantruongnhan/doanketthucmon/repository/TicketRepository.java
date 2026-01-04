package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thantruongnhan.doanketthucmon.entity.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByShowtimeId(Long showtimeId);

    List<Ticket> findBySeatId(Long seatId);
}
