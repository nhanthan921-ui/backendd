package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.entity.*;
import com.thantruongnhan.doanketthucmon.entity.enums.OrderStatus;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentMethod;
import com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus;
import com.thantruongnhan.doanketthucmon.repository.BillRepository;
import com.thantruongnhan.doanketthucmon.repository.OrderRepository;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.repository.PromotionRepository;
import com.thantruongnhan.doanketthucmon.service.OrderService;
import com.thantruongnhan.doanketthucmon.controller.OrderWebSocketController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.thantruongnhan.doanketthucmon.repository.TableRepository;
import com.thantruongnhan.doanketthucmon.entity.enums.Status;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderWebSocketController orderWebSocketController;
    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final TableRepository tableRepository;
    private final PromotionRepository promotionRepository;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderWebSocketController orderWebSocketController,
            BillRepository billRepository,
            ProductRepository productRepository,
            TableRepository tableRepository,
            PromotionRepository promotionRepository) {
        this.orderRepository = orderRepository;
        this.orderWebSocketController = orderWebSocketController;
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.tableRepository = tableRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        System.out.println("========== CREATE ORDER START ==========");

        // 1. Validate và load Table
        if (order.getTable() != null && order.getTable().getId() != null) {
            TableEntity table = tableRepository.findById(order.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn!"));

            // ✅ KIỂM TRA XEM BÀN ĐÃ CÓ ĐƠN CHƯA THANH TOÁN CHƯA
            List<Order> existingOrders = orderRepository.findByTableIdAndStatusNotIn(
                    table.getId(),
                    Arrays.asList(OrderStatus.PAID, OrderStatus.CANCELLED));

            if (!existingOrders.isEmpty()) {
                // Có đơn chưa thanh toán -> GỘP VÀO ĐƠN CŨ
                Order existingOrder = existingOrders.get(0);
                System.out.println(
                        "🔄 Bàn " + table.getNumber() + " đã có đơn #" + existingOrder.getId() + " chưa thanh toán");
                System.out.println("📦 Gộp món mới vào đơn hiện tại...");

                // Thêm các món mới vào đơn hiện có
                if (order.getItems() != null && !order.getItems().isEmpty()) {
                    for (OrderItem newItem : order.getItems()) {
                        if (newItem.getProduct() != null && newItem.getProduct().getId() != null) {
                            Product product = productRepository.findById(newItem.getProduct().getId())
                                    .orElseThrow(() -> new RuntimeException(
                                            "Sản phẩm ID " + newItem.getProduct().getId() + " không tồn tại!"));

                            // Kiểm tra xem sản phẩm đã có trong đơn chưa
                            boolean found = false;
                            for (OrderItem existingItem : existingOrder.getItems()) {
                                if (existingItem.getProduct().getId().equals(product.getId())) {
                                    // Cộng dồn số lượng
                                    existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                                    existingItem.calculateSubtotal();
                                    found = true;
                                    System.out
                                            .println("✅ Cộng dồn: " + product.getName() + " x" + newItem.getQuantity());
                                    break;
                                }
                            }

                            if (!found) {
                                // Thêm món mới
                                OrderItem itemToAdd = new OrderItem();
                                itemToAdd.setOrder(existingOrder);
                                itemToAdd.setProduct(product);
                                itemToAdd.setQuantity(newItem.getQuantity());
                                itemToAdd.setPrice(product.getPrice());
                                itemToAdd.calculateSubtotal();
                                existingOrder.getItems().add(itemToAdd);
                                System.out
                                        .println("✅ Thêm món mới: " + product.getName() + " x" + newItem.getQuantity());
                            }
                        }
                    }
                }

                // ✅ CHUYỂN TRẠNG THÁI VỀ PREPARING NẾU ĐÃ COMPLETED
                if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
                    existingOrder.setStatus(OrderStatus.PREPARING);
                    System.out.println("🔄 Đơn đã hoàn thành -> chuyển về PREPARING");
                }

                // Tính lại tổng tiền
                existingOrder.recalcTotal();
                BigDecimal originalTotal = existingOrder.getTotalAmount();
                System.out.println("💰 Tổng tiền gốc sau khi gộp: " + originalTotal);

                // Áp dụng promotion nếu có
                if (existingOrder.getPromotion() != null && existingOrder.getPromotion().getId() != null) {
                    Promotion promotion = promotionRepository.findById(existingOrder.getPromotion().getId())
                            .orElse(null);

                    if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                        BigDecimal discount = BigDecimal.ZERO;

                        if (promotion.getDiscountPercentage() != null
                                && promotion.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                            discount = originalTotal.multiply(promotion.getDiscountPercentage())
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        } else if (promotion.getDiscountAmount() != null
                                && promotion.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                            discount = promotion.getDiscountAmount();
                        }

                        BigDecimal finalTotal = originalTotal.subtract(discount);
                        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                            finalTotal = BigDecimal.ZERO;
                        }

                        existingOrder.setTotalAmount(finalTotal);
                        System.out.println("🎁 Discount: " + discount);
                        System.out.println("💰 Final Total: " + finalTotal);
                    } else {
                        existingOrder.setPromotion(null);
                    }
                }

                // Cập nhật thời gian
                existingOrder.setUpdatedAt(LocalDateTime.now());

                // Lưu đơn hàng đã gộp
                Order savedOrder = orderRepository.save(existingOrder);

                System.out.println("✅ Đã gộp món vào đơn #" + savedOrder.getId());
                System.out.println("========== CREATE ORDER END (MERGED) ==========");

                // Gửi WebSocket update thay vì new order
                orderWebSocketController.sendOrderUpdate(savedOrder);

                return savedOrder;
            }

            // Nếu không có đơn nào -> tạo mới bình thường
            table.setStatus(Status.OCCUPIED);
            table.setUpdatedAt(LocalDateTime.now());
            tableRepository.save(table);
            order.setTable(table);
        }

        // 2. Xử lý OrderItems cho đơn mới
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            System.out.println("📦 Processing " + order.getItems().size() + " items...");

            List<OrderItem> processedItems = new ArrayList<>();

            for (OrderItem item : order.getItems()) {
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Product product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Sản phẩm ID " + item.getProduct().getId() + " không tồn tại!"));

                    item.setProduct(product);
                    item.setPrice(product.getPrice());
                    item.setOrder(order);
                    item.calculateSubtotal();

                    processedItems.add(item);
                }
            }

            order.setItems(processedItems);
        }

        // 3. Xử lý Promotion (nếu có)
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion promotion = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);

            if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                order.setPromotion(promotion);
                System.out.println("🎁 Promotion applied: " + promotion.getName());
            }
        }

        // 4. Tính tổng tiền (trước khi áp promotion)
        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        System.out.println("💰 Original Total: " + originalTotal);

        // 5. Áp dụng promotion (nếu có)
        if (order.getPromotion() != null) {
            Promotion promo = order.getPromotion();
            BigDecimal discount = BigDecimal.ZERO;

            if (promo.getDiscountPercentage() != null && promo.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                discount = originalTotal.multiply(promo.getDiscountPercentage())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            } else if (promo.getDiscountAmount() != null && promo.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                discount = promo.getDiscountAmount();
            }

            BigDecimal finalTotal = originalTotal.subtract(discount);
            if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                finalTotal = BigDecimal.ZERO;
            }

            order.setTotalAmount(finalTotal);
            System.out.println("🎁 Discount: " + discount);
            System.out.println("💰 Final Total: " + finalTotal);
        }

        // 6. Set thời gian
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // 7. Set trạng thái mặc định
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // 8. Lưu order mới
        Order savedOrder = orderRepository.save(order);

        System.out.println("✅ Order saved with ID: " + savedOrder.getId());
        System.out.println("========== CREATE ORDER END ==========");

        orderWebSocketController.sendNewOrder(savedOrder);

        return savedOrder;
    }

    /**
     * Tính lại tổng tiền sau khi áp dụng khuyến mãi (nếu có).
     */
    private BigDecimal applyPromotion(Order order, BigDecimal originalTotal) {
        if (order.getPromotion() == null) {
            return originalTotal;
        }

        try {
            Promotion promo = order.getPromotion();

            // Nếu khuyến mãi có ngày hết hạn → kiểm tra
            if (promo.getEndDate() != null && promo.getEndDate().isBefore(LocalDate.now())) {
                // Hết hạn thì bỏ khuyến mãi
                order.setPromotion(null);
                return originalTotal;
            }

            // Nếu là giảm theo %
            if (promo.getDiscountPercentage() != null && promo.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = originalTotal.multiply(promo.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100));
                return originalTotal.subtract(discountAmount);
            }

            // Nếu là giảm theo số tiền cố định
            if (promo.getDiscountAmount() != null && promo.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discounted = originalTotal.subtract(promo.getDiscountAmount());
                return discounted.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discounted;
            }

            return originalTotal;
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi áp dụng khuyến mãi: " + e.getMessage());
            return originalTotal;
        }
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderStatus status, PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

        LocalDateTime now = LocalDateTime.now();

        // 1️⃣ Cập nhật trạng thái
        order.setStatus(status);
        order.setUpdatedAt(now);

        // 🧩 FIX: Load lại Promotion từ DB nếu có
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion freshPromo = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);
            if (freshPromo != null && Boolean.TRUE.equals(freshPromo.getIsActive())) {
                order.setPromotion(freshPromo);
            } else {
                order.setPromotion(null); // Nếu không tồn tại hoặc không active → bỏ luôn
            }
        }

        // 2️⃣ Tính lại tổng tiền
        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        BigDecimal finalTotal = applyPromotion(order, originalTotal);
        order.setTotalAmount(finalTotal);

        // 3️⃣ Nếu trạng thái là PAID → tạo Bill nếu chưa có
        if (status == OrderStatus.PAID) {
            if (billRepository.existsByOrderId(order.getId())) {
                throw new RuntimeException("Đơn hàng này đã được tạo hóa đơn trước đó!");
            }

            order.setPaidAt(now);

            Bill bill = Bill.builder()
                    .order(order)
                    .totalAmount(finalTotal)
                    .paymentMethod(paymentMethod)
                    .paymentStatus(PaymentStatus.PAID)
                    .issuedAt(now)
                    .notes("Hóa đơn tự động cho đơn #" + order.getId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            billRepository.save(bill);

            // ✅ Giải phóng bàn
            freeOrUpdateTable(order, Status.FREE);
        }

        // 4️⃣ Nếu đơn bị hủy → giải phóng bàn
        else if (status == OrderStatus.CANCELLED) {
            freeOrUpdateTable(order, Status.FREE);
        }

        // 5️⃣ Nếu hoàn thành → giữ bàn OCCUPIED cho tới khi thanh toán
        else if (status == OrderStatus.COMPLETED) {
            freeOrUpdateTable(order, Status.OCCUPIED);
        }

        // 6️⃣ Lưu lại đơn hàng
        Order updated = orderRepository.save(order);

        // 7️⃣ Phát qua WebSocket để UI cập nhật real-time
        orderWebSocketController.sendOrderUpdate(updated);

        return updated;
    }

    /**
     * Cập nhật trạng thái bàn an toàn
     */
    private void freeOrUpdateTable(Order order, Status status) {
        if (order.getTable() != null && order.getTable().getId() != null) {
            TableEntity table = tableRepository.findById(order.getTable().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn!"));
            table.setStatus(status);
            table.setUpdatedAt(LocalDateTime.now());
            tableRepository.save(table);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
        orderWebSocketController.sendOrderDeleted(id);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> searchOrders(String keyword) {
        return orderRepository.searchOrders(keyword.toLowerCase());
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

        LocalDateTime now = LocalDateTime.now();

        // ✅ CHỈ CẬP NHẬT STATUS, KHÔNG ĐỘNG VÀO PROMOTION
        order.setStatus(status);
        order.setUpdatedAt(now);

        // Xử lý logic bàn
        if (status == OrderStatus.COMPLETED) {
            freeOrUpdateTable(order, Status.OCCUPIED);
        } else if (status == OrderStatus.CANCELLED) {
            freeOrUpdateTable(order, Status.FREE);
        }

        // Lưu đơn hàng
        Order updated = orderRepository.save(order);

        // Phát WebSocket
        orderWebSocketController.sendOrderUpdate(updated);

        return updated;
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status, PaymentMethod paymentMethod) {
        return updateOrder(id, status, paymentMethod);
    }

    @Override
    @Transactional
    public Order addMultipleProductsToOrder(Long orderId, List<Map<String, Object>> newItems) {
        System.out.println("========== ADD MULTIPLE PRODUCTS START ==========");

        // 1. Tải đơn hàng hiện tại
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        System.out.println("📦 Đơn hàng hiện tại: #" + order.getId() + " - Status: " + order.getStatus());

        // 2. Kiểm tra trạng thái đơn hàng
        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Không thể thêm món vào đơn đã thanh toán");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Không thể thêm món vào đơn đã hủy");
        }

        // 3. Lưu trạng thái ban đầu
        boolean wasCompleted = (order.getStatus() == OrderStatus.COMPLETED);

        // 4. Thêm từng sản phẩm vào đơn
        for (Map<String, Object> item : newItems) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer quantity = (Integer) item.get("quantity");

            System.out.println("➕ Thêm sản phẩm #" + productId + " x" + quantity);

            // Load sản phẩm
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + productId + " không tồn tại!"));

            // Kiểm tra xem sản phẩm đã có trong đơn chưa
            boolean productExists = false;
            for (OrderItem existingItem : order.getItems()) {
                if (existingItem.getProduct().getId().equals(productId)) {
                    // Cộng dồn số lượng
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    existingItem.calculateSubtotal();
                    productExists = true;
                    System.out.println("✅ Đã cộng dồn số lượng sản phẩm #" + productId);
                    break;
                }
            }

            // Nếu chưa có, thêm mới
            if (!productExists) {
                OrderItem newItem = new OrderItem();
                newItem.setOrder(order);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                newItem.setPrice(product.getPrice());
                newItem.calculateSubtotal();
                order.getItems().add(newItem);
                System.out.println("✅ Đã thêm sản phẩm mới #" + productId);
            }
        }

        // 5. Tính lại tổng tiền
        order.recalcTotal();
        BigDecimal originalTotal = order.getTotalAmount();
        System.out.println("💰 Tổng tiền gốc: " + originalTotal);

        // 6. Áp dụng promotion nếu có
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion promotion = promotionRepository.findById(order.getPromotion().getId())
                    .orElse(null);

            if (promotion != null && Boolean.TRUE.equals(promotion.getIsActive())) {
                order.setPromotion(promotion);
                BigDecimal finalTotal = applyPromotion(order, originalTotal);
                order.setTotalAmount(finalTotal);
                System.out.println("🎁 Đã áp dụng khuyến mãi: " + promotion.getName());
                System.out.println("💰 Tổng tiền sau khuyến mãi: " + finalTotal);
            } else {
                order.setPromotion(null);
            }
        }

        // 7. Nếu đơn đã hoàn thành, chuyển về đang chuẩn bị
        if (wasCompleted) {
            order.setStatus(OrderStatus.PREPARING);
            System.out.println("🔄 Đơn đã hoàn thành -> chuyển về PREPARING");
        }

        // 8. Cập nhật thời gian
        order.setUpdatedAt(LocalDateTime.now());

        // 9. Lưu đơn hàng
        Order updatedOrder = orderRepository.save(order);

        System.out.println("✅ Đã lưu đơn hàng với tổng tiền: " + updatedOrder.getTotalAmount());
        System.out.println("========== ADD MULTIPLE PRODUCTS END ==========");

        // 10. Phát sự kiện WebSocket
        orderWebSocketController.sendOrderUpdate(updatedOrder);

        return updatedOrder;
    }

    @Override
    @Transactional
    public Order addProductToOrder(Long orderId, Product product, int quantity) {
        System.out.println("========== ADD SINGLE PRODUCT START ==========");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + orderId));

        // Kiểm tra trạng thái
        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Không thể thêm món vào đơn đã thanh toán");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Không thể thêm món vào đơn đã hủy");
        }

        boolean wasCompleted = (order.getStatus() == OrderStatus.COMPLETED);

        // Kiểm tra sản phẩm đã có chưa
        boolean productExists = false;
        for (OrderItem existingItem : order.getItems()) {
            if (existingItem.getProduct().getId().equals(product.getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.calculateSubtotal();
                productExists = true;
                break;
            }
        }

        if (!productExists) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            item.calculateSubtotal();
            order.getItems().add(item);
        }

        // Tính lại tổng
        order.recalcTotal();
        BigDecimal finalTotal = applyPromotion(order, order.getTotalAmount());
        order.setTotalAmount(finalTotal);

        // Nếu đã hoàn thành -> chuyển về preparing
        if (wasCompleted) {
            order.setStatus(OrderStatus.PREPARING);
        }

        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);

        System.out.println("✅ Thêm sản phẩm thành công");
        System.out.println("========== ADD SINGLE PRODUCT END ==========");

        orderWebSocketController.sendOrderUpdate(updated);
        return updated;
    }
}