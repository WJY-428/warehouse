package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.InboundRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboundRecordRepository extends JpaRepository<InboundRecord, Long> {
    // 根据产品ID查询入库记录
    List<InboundRecord> findByProductId(Long productId);
}