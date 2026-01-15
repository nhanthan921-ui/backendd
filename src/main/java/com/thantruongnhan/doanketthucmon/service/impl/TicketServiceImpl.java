package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.Showtime;
import com.thantruongnhan.doanketthucmon.entity.Ticket;
import com.thantruongnhan.doanketthucmon.repository.SeatRepository;
import com.thantruongnhan.doanketthucmon.repository.ShowtimeRepository;
import com.thantruongnhan.doanketthucmon.repository.TicketRepository;
import com.thantruongnhan.doanketthucmon.service.TicketService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Override
    public Ticket createTicket(Ticket ticket) {

        if (ticket.getShowtime() == null || ticket.getShowtime().getId() == null) {
            throw new RuntimeException("Showtime is required");
        }

        if (ticket.getSeat() == null || ticket.getSeat().getId() == null) {
            throw new RuntimeException("Seat is required");
        }

        Showtime showtime = showtimeRepository.findById(ticket.getShowtime().getId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Seat seat = seatRepository.findById(ticket.getSeat().getId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        ticket.setShowtime(showtime);
        ticket.setSeat(seat);

        return ticketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> getTicketsByShowtime(Long showtimeId) {
        return ticketRepository.findByShowtimeId(showtimeId);
    }

    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}
