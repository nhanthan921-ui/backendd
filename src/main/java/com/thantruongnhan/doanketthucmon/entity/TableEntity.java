package com.thantruongnhan.doanketthucmon.entity;

import com.thantruongnhan.doanketthucmon.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tables")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer number;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private Status status = Status.FREE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
