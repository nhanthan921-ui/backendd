package com.thantruongnhan.doanketthucmon.entity.enums;

public enum SeatStatus {
    AVAILABLE("Trống"),
    SELECTED("Đang chọn"),
    BOOKED("Đã đặt");

    private String displayName;

    SeatStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}