package com.warehouse.warehouse_management.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入库记录。入库 = 写一条记录 + 商品库存累加，两件事必须在同一个事务里完成，
 * 见 InboundRecordService#create。
 */
@Data
@Entity
@Table(name = "inbound_record")
public class InboundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;        // 关联商品

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;    // 关联仓库

    @Column(nullable = false)
    private Integer quantity;       // 入库数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "inbound_time", nullable = false)
    private LocalDateTime inboundTime; // 入库时间

    @PrePersist
    public void prePersist() {
        if (inboundTime == null) {
            inboundTime = LocalDateTime.now();
        }
    }
}
