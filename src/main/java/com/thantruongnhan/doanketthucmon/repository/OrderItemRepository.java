package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
