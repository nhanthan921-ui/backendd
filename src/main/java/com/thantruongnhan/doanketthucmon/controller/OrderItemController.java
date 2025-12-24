package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.OrderItem;
import com.thantruongnhan.doanketthucmon.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    // Xem tất cả order items (Admin, Nhân viên)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<OrderItem> getAllItems() {
        return orderItemService.getAllItems();
    }

    // Xem 1 order item (Admin, Nhân viên)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public OrderItem getItemById(@PathVariable Long id) {
        return orderItemService.getItemById(id);
    }

    // Thêm món vào đơn (Admin, Nhân viên)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public OrderItem createItem(@RequestBody OrderItem item) {
        return orderItemService.createItem(item);
    }

    // Cập nhật món trong đơn (Admin, Nhân viên)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public OrderItem updateItem(@PathVariable Long id, @RequestBody OrderItem item) {
        return orderItemService.updateItem(id, item);
    }

    // Xóa món khỏi đơn (Admin, Nhân viên)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void deleteItem(@PathVariable Long id) {
        orderItemService.deleteItem(id);
    }
}
