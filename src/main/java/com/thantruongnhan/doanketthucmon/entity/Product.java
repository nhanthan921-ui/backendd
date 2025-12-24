package com.thantruongnhan.doanketthucmon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String imageUrl;
    private Integer stockQuantity = 0;
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    @ToString.Exclude
    private Set<Promotion> promotions;
}
