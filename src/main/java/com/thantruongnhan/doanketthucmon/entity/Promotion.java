package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "promotions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(name = "promotion_products", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JsonIgnore
    @ToString.Exclude
    private Set<Product> products;

    @Transient
    private List<Long> productIds;
}
