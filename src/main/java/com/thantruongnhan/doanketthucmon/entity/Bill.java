package com.thantruongnhan.doanketthucmon.entity;

import com.thantruongnhan.doanketthucmon.entity.enums.PaymentMethod;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDateTime issuedAt;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}