package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inbound_record")
public class InboundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;        // 关联商品
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;    // 关联仓库
    private Integer quantity;       // 入库数量
    private LocalDateTime inboundTime; // 入库时间

    @PrePersist
    public void prePersist() {
        if (inboundTime == null) {
            inboundTime = LocalDateTime.now();
        }
    }
}