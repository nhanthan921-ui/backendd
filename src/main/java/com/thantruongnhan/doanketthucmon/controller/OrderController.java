package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Order;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentMethod;
import com.thantruongnhan.doanketthucmon.service.OrderService;
import com.thantruongnhan.doanketthucmon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final OrderWebSocketController orderWebSocketController;

    @Autowired
    public OrderController(OrderService orderService,
            ProductService productService,
            OrderWebSocketController orderWebSocketController) {
        this.orderService = orderService;
        this.productService = productService;
        this.orderWebSocketController = orderWebSocketController;
    }

    // Lấy danh sách tất cả đơn hàng
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Xem chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Tạo đơn hàng mới → realtime gửi cho nhân viên (barista)
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        System.out.println("Received order: " + order);
        Order savedOrder = orderService.createOrder(order);
        orderWebSocketController.sendNewOrder(savedOrder);
        return savedOrder;
    }

    // Thêm sản phẩm vào đơn hàng
    @PostMapping("/{orderId}/add-product")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Order addProductToOrder(@PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        Long productId = ((Number) body.get("productId")).longValue();
        Integer quantity = (Integer) body.get("quantity");

        Product product = productService.getProductById(productId);
        Order updatedOrder = orderService.addProductToOrder(orderId, product, quantity);

        orderWebSocketController.sendOrderUpdate(updatedOrder); // realtime update
        return updatedOrder;
    }

    // Thêm món vào đơn hàng đã tồn tại
    @PostMapping("/{orderId}/add-items")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<?> addItemsToExistingOrder(
            @PathVariable Long orderId,
            @RequestBody List<Map<String, Object>> newItems) {

        try {
            System.out.println("🔵 Nhận yêu cầu thêm món vào đơn #" + orderId);
            System.out.println("🔵 Items: " + newItems);

            Order existingOrder = orderService.getOrderById(orderId);

            if (existingOrder == null) {
                return ResponseEntity.status(404).body("Không tìm thấy đơn hàng #" + orderId);
            }

            System.out.println("🔵 Đơn hiện tại: status=" + existingOrder.getStatus());

            // Không cho phép thêm món vào đơn đã thanh toán hoặc đã hủy
            if (existingOrder.getStatus() == OrderStatus.PAID) {
                return ResponseEntity.badRequest().body("Không thể thêm món vào đơn đã thanh toán");
            }

            if (existingOrder.getStatus() == OrderStatus.CANCELLED) {
                return ResponseEntity.badRequest().body("Không thể thêm món vào đơn đã hủy");
            }

            // Thêm từng sản phẩm vào đơn
            for (Map<String, Object> item : newItems) {
                Long productId = ((Number) item.get("productId")).longValue();
                Integer quantity = (Integer) item.get("quantity");

                System.out.println("🔵 Thêm sản phẩm #" + productId + " x" + quantity);

                Product product = productService.getProductById(productId);
                existingOrder = orderService.addProductToOrder(orderId, product, quantity);
            }

            // Nếu đơn đã completed, chuyển về preparing
            if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
                System.out.println("🔵 Đơn đã hoàn thành, chuyển về PREPARING");
                existingOrder = orderService.updateOrderStatus(orderId, OrderStatus.PREPARING);
            }

            System.out.println("✅ Thêm món thành công! Total: " + existingOrder.getTotalAmount());

            // Gửi update qua WebSocket
            orderWebSocketController.sendOrderUpdate(existingOrder);

            return ResponseEntity.ok(existingOrder);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi thêm món: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }

    // Cập nhật thông tin đơn hàng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE','CUSTOMER')")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") OrderStatus status,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") PaymentMethod paymentMethod) {

        return orderService.updateOrder(id, status, paymentMethod);
    }

    // Xóa đơn hàng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        orderWebSocketController.sendOrderDeleted(id); // realtime delete
    }

    // Tìm kiếm đơn hàng theo từ khóa
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Order> searchOrders(@RequestParam("keyword") String keyword) {
        return orderService.searchOrders(keyword);
    }

    // Các hành động cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Order confirmOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.CONFIRMED);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @PutMapping("/{id}/prepare")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Order prepareOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.PREPARING);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Order completeOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.COMPLETED);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<?> customerPayOrder(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {

        try {
            // Chuyển đổi String sang enum PaymentMethod (CASH hoặc TRANSFER)
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod.toUpperCase());

            orderService.updateOrder(id, OrderStatus.PAID, method);
            return ResponseEntity.ok("Thanh toán thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Phương thức thanh toán không hợp lệ!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi cập nhật thanh toán!");
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Order cancelOrder(@PathVariable Long id) {
        Order updated = orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getPendingOrders() {
        return orderService.getAllOrders()
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .collect(Collectors.toList());
    }

}
