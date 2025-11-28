package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}