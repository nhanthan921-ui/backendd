package com.thantruongnhan.doanketthucmon.dto;

import com.thantruongnhan.doanketthucmon.entity.Room;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.SeatType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDTO {
    private Long id;
    private String rowSeat;
    private Integer number;
    private SeatType type;
    private SeatStatus status;
    private Room room;
}