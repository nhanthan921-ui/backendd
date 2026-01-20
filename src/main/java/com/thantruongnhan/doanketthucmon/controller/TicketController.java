package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.dto.CreateTicketRequest;
import com.thantruongnhan.doanketthucmon.dto.TicketResponse;
import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.Ticket;
import com.thantruongnhan.doanketthucmon.service.TicketService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/customer/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<TicketResponse> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest request) {
        try {
            log.info("📥 Received create ticket request: {}", request);

            Ticket ticket = ticketService.createTicket(
                    request.getShowtimeId(),
                    request.getSeatId(),
                    request.getUserId());

            log.info("Ticket created: {}", ticket.getId());
            return ResponseEntity.ok(ticket);

        } catch (IllegalArgumentException e) {
            log.error("❌ Bad request: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (IllegalStateException e) {
            log.error("❌ Conflict: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

        } catch (Exception e) {
            log.error("❌ Internal error", e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<?> deleteTicket(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            log.info("📥 Delete ticket request: ticketId={}, userId={}", id, userId);

            Ticket ticket = ticketService.getTicketById(id);

            // ADMIN có thể xóa bất kỳ vé nào
            // CUSTOMER chỉ được xóa vé của mình
            if (userId != null && !ticket.getUser().getId().equals(userId)) {
                log.error("❌ User {} attempted to delete ticket {} owned by {}",
                        userId, id, ticket.getUser().getId());
                Map<String, String> error = new HashMap<>();
                error.put("message", "Bạn không có quyền xóa vé này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            ticketService.deleteTicket(id);

            log.info("Ticket {} deleted successfully", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã xóa vé thành công");
            response.put("ticketId", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error deleting ticket {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Không thể xóa vé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/showtime/{showtimeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Ticket> getTicketsByShowtime(@PathVariable Long showtimeId) {
        return ticketService.getTicketsByShowtime(showtimeId);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<?> getTicketsByUserId(@PathVariable Long userId) {
        try {
            log.info("📥 Fetching tickets for user: {}", userId);

            List<Ticket> tickets = ticketService.getTicketsByUserId(userId);

            log.info("Found {} tickets for user {}", tickets.size(), userId);
            return ResponseEntity.ok(tickets);

        } catch (Exception e) {
            log.error("❌ Error fetching tickets for user {}: {}", userId, e.getMessage(), e);

            Map<String, String> error = new HashMap<>();
            error.put("message", "Không thể lấy danh sách vé: " + e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
