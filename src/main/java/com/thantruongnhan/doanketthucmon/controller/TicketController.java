package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.dto.CreateTicketRequest;
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
    public ResponseEntity<Ticket> createTicket(
            @RequestBody CreateTicketRequest request) {

        Ticket ticket = ticketService.createTicket(
                request.getShowtimeId(),
                request.getSeatId(),
                request.getUserId());

        return ResponseEntity.ok(ticket);
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

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Ticket>> getTicketsByUserId(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }
}
