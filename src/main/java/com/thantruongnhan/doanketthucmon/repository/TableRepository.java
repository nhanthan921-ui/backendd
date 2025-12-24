package com.thantruongnhan.doanketthucmon.repository;

import com.thantruongnhan.doanketthucmon.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
}
