package com.thantruongnhan.doanketthucmon.controller;

import com.thantruongnhan.doanketthucmon.dto.PromotionDTO;
import com.thantruongnhan.doanketthucmon.entity.Promotion;
import com.thantruongnhan.doanketthucmon.repository.PromotionRepository;
import com.thantruongnhan.doanketthucmon.service.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/promotions")
@CrossOrigin(origins = "http://localhost:3000")
public class PromotionController {
    @Autowired
    private PromotionRepository promotionRepository;

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public List<Promotion> getAllPromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive())
                        && (p.getStartDate() == null || !p.getStartDate().isAfter(today))
                        && (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                .collect(Collectors.toList());
    }

    @GetMapping("/all") // nếu muốn lấy hết (kể cả hết hạn)
    public List<Promotion> getAllIncludingExpired() {
        return promotionRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PromotionDTO createPromotion(@RequestBody PromotionDTO promotionDTO) {
        return promotionService.createPromotion(promotionDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable Long id,
            @RequestBody PromotionDTO promotionDTO) {
        try {
            return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
