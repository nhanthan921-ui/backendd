package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Order;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
                SELECT o FROM Order o
                WHERE CAST(o.table.number AS string) LIKE CONCAT('%', :keyword, '%')
                   OR CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%')
            """)
    List<Order> searchOrders(@Param("keyword") String keyword);

    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status NOT IN ('PAID', 'CANCELED')")
    List<Order> findUnpaidOrdersByTableId(@Param("tableId") Long tableId);

    List<Order> findByTableIdAndStatusNotIn(Long id, List<OrderStatus> asList);
}
