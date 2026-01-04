package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.entity.Ticket;
import com.thantruongnhan.doanketthucmon.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/customer/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public Ticket createTicket(
            @RequestParam Long showtimeId,
            @RequestParam Long seatId,
            @RequestParam Integer price) {

        return ticketService.createTicket(showtimeId, seatId, price);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    @GetMapping("/showtime/{showtimeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Ticket> getTicketsByShowtime(@PathVariable Long showtimeId) {
        return ticketService.getTicketsByShowtime(showtimeId);
    }
}
