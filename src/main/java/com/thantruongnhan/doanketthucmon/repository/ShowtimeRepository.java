package com.thantruongnhan.doanketthucmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thantruongnhan.doanketthucmon.entity.Showtime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
}