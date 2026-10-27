package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    /** 关键字模糊搜索：SKU / 名称 / 分类 */
    @Query("""
            select p from Product p
            where lower(p.sku) like lower(concat('%', :keyword, '%'))
               or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(p.category, '')) like lower(concat('%', :keyword, '%'))
            """)
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);
}
