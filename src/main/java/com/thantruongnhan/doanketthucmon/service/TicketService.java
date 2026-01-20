package com.thantruongnhan.doanketthucmon.service;

import com.thantruongnhan.doanketthucmon.dto.TicketResponse;
import com.thantruongnhan.doanketthucmon.entity.Ticket;

import java.util.List;

public interface TicketService {

    List<TicketResponse> getAllTickets();

    Ticket getTicketById(Long id);

    Ticket createTicket(Long showtimeId, Long seatId, Long userId);

    void deleteTicket(Long id);

    List<Ticket> getTicketsByShowtime(Long showtimeId);

    List<Ticket> getTicketsByUserId(Long userId);

    Ticket cancelTicket(Long ticketId);
}
