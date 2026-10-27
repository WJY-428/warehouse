package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID

    @NotBlank(message = "SKU 不能为空")
    @Size(max = 64, message = "SKU 最多 64 个字符")
    @Column(unique = true, nullable = false, length = 64)
    private String sku;             // 商品SKU（唯一）

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 128, message = "商品名称最多 128 个字符")
    @Column(nullable = false, length = 128)
    private String name;            // 商品名称

    @Size(max = 64, message = "分类最多 64 个字符")
    @Column(length = 64)
    private String category;        // 商品分类

    /** 金额用 BigDecimal，避免 double 的精度误差（0.1+0.2 问题） */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;       // 商品价格

    @Min(value = 0, message = "库存不能为负数")
    @Column(nullable = false)
    private Integer stock = 0;      // 库存数量（由入库单累加）
}
