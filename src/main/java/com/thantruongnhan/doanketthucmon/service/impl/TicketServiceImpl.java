package com.thantruongnhan.doanketthucmon.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
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
            log.info("✅ Seat found: {} - Status: {}", seat.getId(), seat.getStatus());

            // ✅ 3. Kiểm tra status của ghế (handle null)
            SeatStatus seatStatus = seat.getStatus();
            if (seatStatus == null) {
                log.warn("⚠️ Seat {} has null status, treating as AVAILABLE", seatId);
                // Có thể set default nếu null
                seat.setStatus(SeatStatus.AVAILABLE);
                seatStatus = SeatStatus.AVAILABLE;
            }

            if (seatStatus != SeatStatus.AVAILABLE) {
                log.error("❌ Seat {} is not available. Current status: {}", seatId, seatStatus);
                throw new IllegalStateException("Ghế không khả dụng. Trạng thái hiện tại: " + seatStatus);
            }

            // ✅ 4. Chống đặt trùng ghế
            if (ticketRepository.existsByShowtimeIdAndSeatId(showtimeId, seatId)) {
                log.error("❌ Seat {} already booked for showtime {}", seatId, showtimeId);
                throw new IllegalStateException("Ghế đã được đặt cho suất chiếu này!");
            }

            // ✅ 5. Validate User
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("❌ User not found: {}", userId);
                        return new IllegalArgumentException("Không tìm thấy người dùng với ID: " + userId);
                    });
            log.info("✅ User found: {}", user.getId());

            // ✅ 6. Tạo ticket
            Ticket ticket = new Ticket();
            ticket.setShowtime(showtime);
            ticket.setSeat(seat);
            ticket.setUser(user);
            ticket.setPrice(showtime.getPrice());
            ticket.setStatus(TicketStatus.PENDING); // ✅ Đảm bảo enum này tồn tại
            ticket.setBookedAt(LocalDateTime.now());
            ticket.setTicketCode(UUID.randomUUID().toString());

            // ✅ 7. Cập nhật trạng thái ghế
            seat.setStatus(SeatStatus.RESERVED);
            seatRepository.save(seat);
            log.info("✅ Seat {} status updated to RESERVED", seatId);

            // ✅ 8. Lưu ticket
            Ticket savedTicket = ticketRepository.save(ticket);
            log.info("🎉 Ticket created successfully: {}", savedTicket.getId());

            return savedTicket;

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Lỗi validation - ném lại để controller bắt
            log.error("❌ Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Lỗi không mong đợi
            log.error("❌ Unexpected error creating ticket", e);
            e.printStackTrace();
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

    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}
