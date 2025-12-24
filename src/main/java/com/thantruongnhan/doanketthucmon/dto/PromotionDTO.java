package com.thantruongnhan.doanketthucmon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PromotionDTO {
    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductDTO> products;
}
