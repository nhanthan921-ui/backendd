package com.thantruongnhan.doanketthucmon.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 50)
    private String orderCode;

    @Column(length = 100)
    private String customerName;

    @Column(length = 20)
    private String customerPhone;

    @Column(length = 100)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20) // ✅ FIX: status -> order_status
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("order")
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("order")
    @Builder.Default
    private List<OrderCombo> orderCombos = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("order")
    private Payment payment;

    public void recalcTotal() {
        BigDecimal ticketTotal = BigDecimal.ZERO;
        BigDecimal comboTotal = BigDecimal.ZERO;

        if (tickets != null && !tickets.isEmpty()) {
            ticketTotal = tickets.stream()
                    .map(ticket -> BigDecimal.valueOf(ticket.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (orderCombos != null && !orderCombos.isEmpty()) {
            comboTotal = orderCombos.stream()
                    .map(orderCombo -> {
                        orderCombo.calculateTotalPrice();
                        return orderCombo.getTotalPrice();
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        this.totalAmount = ticketTotal.add(comboTotal);

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
        this.finalAmount = this.totalAmount.subtract(discountAmount);

        if (this.finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.finalAmount = BigDecimal.ZERO;
        }
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (orderCode == null) {
            orderCode = "ORD" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}