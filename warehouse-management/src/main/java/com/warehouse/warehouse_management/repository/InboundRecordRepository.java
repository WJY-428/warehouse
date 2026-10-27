package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.InboundRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InboundRecordRepository extends JpaRepository<InboundRecord, Long> {

    /**
     * 按商品ID查入库记录。
     * 注意：实体里的字段是 product（不是 productId），
     * 所以派生查询必须写成 findByProduct_Id，否则 Spring Data 启动时会抛
     * PropertyReferenceException（原来的 findByProductId 就是这个 bug）。
     */
    List<InboundRecord> findByProduct_Id(Long productId);

    boolean existsByWarehouse_Id(Long warehouseId);

    long countByProduct_Id(Long productId);

    /** 删除商品前清理它的入库记录 */
    @Modifying
    @Query("delete from InboundRecord r where r.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    /** 关键字模糊搜索：商品名称 / 仓库名称 */
    @Query("""
            select r from InboundRecord r
            where lower(r.product.name) like lower(concat('%', :keyword, '%'))
               or lower(r.warehouse.name) like lower(concat('%', :keyword, '%'))
            """)
    Page<InboundRecord> search(@Param("keyword") String keyword, Pageable pageable);
}
