package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customer/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Xem tất cả sản phẩm (Admin + Nhân viên)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Xem chi tiết sản phẩm (Admin + Nhân viên)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Tìm kiếm sản phẩm theo tên (Admin + Nhân viên)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Product> searchProducts(@RequestParam("keyword") String keyword) {
        return productService.searchProducts(keyword);
    }

    // Thêm sản phẩm (Chỉ Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return productService.createProduct(name, price, stockQuantity, categoryId, image);
    }

    // Cập nhật sản phẩm (Chỉ Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return productService.updateProduct(id, name, price, stockQuantity, categoryId, image);
    }

    // Lọc sản phẩm theo category
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    // Xóa sản phẩm (Chỉ Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
