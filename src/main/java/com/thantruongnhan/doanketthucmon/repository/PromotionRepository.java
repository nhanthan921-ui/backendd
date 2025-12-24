package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("select distinct p from Promotion p left join fetch p.products")
    List<Promotion> findAllWithProducts();

    @Query("select p from Promotion p left join fetch p.products where p.id = :id")
    Optional<Promotion> findByIdWithProducts(@Param("id") Long id);
}
