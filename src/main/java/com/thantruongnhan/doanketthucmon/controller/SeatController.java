package com.thantruongnhan.doanketthucmon.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thantruongnhan.doanketthucmon.dto.SeatDTO;
import com.thantruongnhan.doanketthucmon.entity.Seat;
import com.thantruongnhan.doanketthucmon.entity.Ticket;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatStatus;
import com.thantruongnhan.doanketthucmon.repository.SeatRepository;
import com.thantruongnhan.doanketthucmon.repository.TicketRepository;
import com.thantruongnhan.doanketthucmon.service.SeatService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class SeatController {

    private final SeatService seatService;
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Seat getSeatById(@PathVariable Long id) {
        return seatService.getSeatById(id);
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Seat> getSeatsByRoom(@PathVariable Long roomId) {
        return seatService.getSeatsByRoom(roomId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Seat createSeat(@RequestBody Seat seat) {
        return seatService.createSeat(seat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Seat updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        return seatService.updateSeat(id, seat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<SeatDTO>> getSeatsByShowtime(@PathVariable Long showtimeId) {
        // 1. Lấy tất cả seats của room (thông qua showtime)
        List<Seat> allSeats = seatRepository.findSeatsByShowtime(showtimeId);

        // 2. Lấy tất cả tickets của showtime này
        List<Ticket> tickets = ticketRepository.findByShowtimeId(showtimeId);

        // 3. Lấy danh sách seat IDs đã được đặt (BOOKED hoặc PENDING)
        Set<Long> bookedSeatIds = tickets.stream()
                .filter(ticket -> ticket.getStatus().equals("BOOKED") ||
                        ticket.getStatus().equals("PENDING"))
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());

        // 4. Map sang DTO với status động
        List<SeatDTO> seatDTOs = allSeats.stream()
                .map(seat -> {
                    SeatDTO dto = new SeatDTO();
                    dto.setId(seat.getId());
                    dto.setRowSeat(seat.getRowSeat());
                    dto.setNumber(seat.getNumber());
                    dto.setType(seat.getType());
                    dto.setRoom(seat.getRoom());

                    // ✅ Status động: nếu có trong bookedSeatIds thì BOOKED, không thì AVAILABLE
                    if (bookedSeatIds.contains(seat.getId())) {
                        dto.setStatus(SeatStatus.BOOKED);
                    } else {
                        dto.setStatus(SeatStatus.AVAILABLE);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(seatDTOs);
    }
}
