package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thantruongnhan.doanketthucmon.dto.TicketResponse;
import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.Showtime;
import com.thantruongnhan.doanketthucmon.entity.Ticket;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.TicketStatus;
import com.thantruongnhan.doanketthucmon.repository.SeatRepository;
import com.thantruongnhan.doanketthucmon.repository.ShowtimeRepository;
import com.thantruongnhan.doanketthucmon.repository.TicketRepository;
import com.thantruongnhan.doanketthucmon.repository.UserRepository;
import com.thantruongnhan.doanketthucmon.service.TicketService;
import com.thantruongnhan.doanketthucmon.entity.User;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Override
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(t -> {
                    TicketResponse dto = new TicketResponse();
                    dto.setId(t.getId());
                    dto.setShowtimeId(t.getShowtime().getId());
                    dto.setSeatId(t.getSeat().getId());
                    dto.setUserId(t.getUser().getId());
                    dto.setPrice(t.getPrice());
                    dto.setStatus(t.getStatus().name());
                    dto.setBookedAt(t.getBookedAt());
                    dto.setTicketCode(t.getTicketCode());
                    return dto;
                })
                .toList();
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Override
    @Transactional
    public Ticket createTicket(Long showtimeId, Long seatId, Long userId) {
        try {
            log.info("🎫 Creating ticket - Showtime: {}, Seat: {}, User: {}",
                    showtimeId, seatId, userId);

            // ✅ 1. Validate Showtime
            Showtime showtime = showtimeRepository.findById(showtimeId)
                    .orElseThrow(() -> {
                        log.error("❌ Showtime not found: {}", showtimeId);
                        return new IllegalArgumentException("Không tìm thấy suất chiếu với ID: " + showtimeId);
                    });
            log.info("✅ Showtime found: {}", showtime.getId());

            // ✅ 2. Validate Seat
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> {
                        log.error("❌ Seat not found: {}", seatId);
                        return new IllegalArgumentException("Không tìm thấy ghế với ID: " + seatId);
                    });
            log.info("✅ Seat found: {}", seat.getId());

            // ❌ XÓA PHẦN KIỂM TRA seat.getStatus() - Seat không còn trường status
            // SeatStatus seatStatus = seat.getStatus();
            // if (seatStatus != SeatStatus.AVAILABLE) { ... }

            // ✅ 3. Chống đặt trùng ghế cho showtime này
            if (ticketRepository.existsByShowtimeIdAndSeatId(showtimeId, seatId)) {
                log.error("❌ Seat {} already booked for showtime {}", seatId, showtimeId);
                throw new IllegalStateException("Ghế đã được đặt cho suất chiếu này!");
            }

            // ✅ 4. Validate User
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("❌ User not found: {}", userId);
                        return new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId);
                    });
            log.info("✅ User found: {}", user.getId());

            // ✅ 5. Tạo ticket
            Ticket ticket = new Ticket();
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setUser(user);
            ticket.setPrice(showtime.getPrice());
            ticket.setStatus(TicketStatus.PENDING);
            ticket.setBookedAt(LocalDateTime.now());
            ticket.setTicketCode(UUID.randomUUID().toString());

            // ✅ 6. Lưu ticket
            Ticket savedTicket = ticketRepository.save(ticket);
            log.info("🎉 Ticket created successfully: {}", savedTicket.getId());

            return savedTicket;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("❌ Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error creating ticket", e);
            throw new RuntimeException("Lỗi không xác định khi tạo vé: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> getTicketsByShowtime(Long showtimeId) {
        return ticketRepository.findByShowtimeId(showtimeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserId(Long userId) {
        try {
            log.info("🔍 Searching tickets for user ID: {}", userId);

            List<Ticket> tickets = ticketRepository.findByUserId(userId);

            log.info("✅ Found {} tickets for user {}", tickets.size(), userId);

            return tickets;

        } catch (Exception e) {
            log.error("❌ Error in getTicketsByUserId: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lấy danh sách vé: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Ticket cancelTicket(Long ticketId) {
        log.info("🔄 Cancelling ticket: {}", ticketId);

        // Tìm vé
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> {
                    log.error("❌ Ticket not found: {}", ticketId);
                    return new IllegalArgumentException("Không tìm thấy vé với ID: " + ticketId);
                });

        // Kiểm tra trạng thái hiện tại
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            log.warn("⚠️ Ticket {} is already cancelled", ticketId);
            throw new IllegalStateException("Vé đã được hủy trước đó");
        }

        if (ticket.getStatus() == TicketStatus.USED) {
            log.warn("⚠️ Ticket {} is already used", ticketId);
            throw new IllegalStateException("Không thể hủy vé đã sử dụng");
        }

        // ✅ CHỈ cập nhật trạng thái ticket thành CANCELLED
        ticket.setStatus(TicketStatus.CANCELLED);

        // Lưu vé đã hủy
        Ticket cancelledTicket = ticketRepository.save(ticket);

        log.info("✅ Ticket {} cancelled successfully. Seat {} will be AVAILABLE for other showtimes automatically",
                ticketId, ticket.getSeat().getId());

        return cancelledTicket;
    }
}
