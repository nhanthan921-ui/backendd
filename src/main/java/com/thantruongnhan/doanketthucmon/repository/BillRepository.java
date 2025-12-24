package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    // Kiểm tra xem đã có hóa đơn cho đơn hàng chưa
    boolean existsByOrderId(Long orderId);

    // Tổng doanh thu của tất cả hóa đơn đã thanh toán
    @Query("""
            SELECT SUM(b.totalAmount)
            FROM Bill b
            WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
            """)
    Double getTotalRevenue();

    // Doanh thu theo từng ngày (ngày - tổng tiền)
    @Query("""
            SELECT DATE(b.createdAt), SUM(b.totalAmount)
            FROM Bill b
            WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
            GROUP BY DATE(b.createdAt)
            ORDER BY DATE(b.createdAt)
            """)
    List<Object[]> getDailyRevenue();

    // Doanh thu trong khoảng thời gian (từ ngày -> đến ngày)
    @Query("""
            SELECT SUM(b.totalAmount)
            FROM Bill b
            WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
            AND DATE(b.createdAt) BETWEEN :startDate AND :endDate
            """)
    Double getRevenueByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
