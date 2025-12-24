package com.thantruongnhan.doanketthucmon.service.impl;

import com.thantruongnhan.doanketthucmon.dto.ProductDTO;
import com.thantruongnhan.doanketthucmon.dto.PromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.Product;
import com.thantruongnhan.doanketthucmon.entity.Promotion;
import com.thantruongnhan.doanketthucmon.repository.ProductRepository;
import com.thantruongnhan.doanketthucmon.repository.PromotionRepository;
import com.thantruongnhan.doanketthucmon.service.PromotionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }

    // ===================== GET ALL =====================
    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAllWithProducts()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ===================== GET BY ID =====================
    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findByIdWithProducts(id)
                .map(this::mapToDTO);
    }

    // ===================== CREATE =====================
    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = mapToEntity(promotionDTO);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        Promotion saved = promotionRepository.save(promotion);
        return mapToDTO(saved);
    }

    // ===================== UPDATE =====================
    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        return promotionRepository.findByIdWithProducts(id)
                .map(existing -> {
                    existing.setName(promotionDTO.getName());
                    existing.setDiscountPercentage(promotionDTO.getDiscountPercentage());
                    existing.setDiscountAmount(promotionDTO.getDiscountAmount());
                    existing.setStartDate(promotionDTO.getStartDate());
                    existing.setEndDate(promotionDTO.getEndDate());
                    existing.setIsActive(promotionDTO.getIsActive());
                    existing.setUpdatedAt(LocalDateTime.now());

                    // Cập nhật danh sách products (chỉ dựa trên products)
                    Set<Product> products = new HashSet<>();
                    if (promotionDTO.getProducts() != null && !promotionDTO.getProducts().isEmpty()) {
                        Set<Long> ids = promotionDTO.getProducts().stream()
                                .map(ProductDTO::getId)
                                .collect(Collectors.toSet());
                        products.addAll(productRepository.findAllById(ids));
                    }
                    existing.setProducts(products);

                    Promotion updated = promotionRepository.save(existing);
                    return mapToDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Promotion not found with id " + id));
    }

    // ===================== DELETE =====================
    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    // ===================== MAPPING =====================
    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setDiscountAmount(promotion.getDiscountAmount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setIsActive(promotion.getIsActive());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());

        if (promotion.getProducts() != null && !promotion.getProducts().isEmpty()) {
            dto.setProducts(
                    promotion.getProducts().stream()
                            .map(this::mapProductToDTO)
                            .toList());
        }

        return dto;
    }

    private Promotion mapToEntity(PromotionDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setId(dto.getId());
        promotion.setName(dto.getName());
        promotion.setDiscountPercentage(dto.getDiscountPercentage());
        promotion.setDiscountAmount(dto.getDiscountAmount());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setIsActive(dto.getIsActive());

        // Lấy danh sách sản phẩm từ dto.products
        Set<Product> products = new HashSet<>();
        if (dto.getProducts() != null && !dto.getProducts().isEmpty()) {
            Set<Long> ids = dto.getProducts().stream()
                    .map(ProductDTO::getId)
                    .collect(Collectors.toSet());
            products.addAll(productRepository.findAllById(ids));
        }
        promotion.setProducts(products);

        return promotion;
    }

    private ProductDTO mapProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsActive(product.getIsActive());
        return dto;
    }
}
