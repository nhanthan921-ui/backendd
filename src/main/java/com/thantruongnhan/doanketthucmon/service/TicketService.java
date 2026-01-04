package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.entity.Ticket;

import java.util.List;

public interface TicketService {

    List<Ticket> getAllTickets();

    Ticket getTicketById(Long id);

    Ticket createTicket(Long showtimeId, Long seatId, Integer price);

    void deleteTicket(Long id);

    List<Ticket> getTicketsByShowtime(Long showtimeId);
}
