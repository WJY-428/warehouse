package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);

    /** 关键字模糊搜索：编码 / 名称 / 地址 / 管理员 */
    @Query("""
            select w from Warehouse w
            where lower(w.code) like lower(concat('%', :keyword, '%'))
               or lower(w.name) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(w.address, '')) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(w.manager, '')) like lower(concat('%', :keyword, '%'))
            """)
    Page<Warehouse> search(@Param("keyword") String keyword, Pageable pageable);
}
