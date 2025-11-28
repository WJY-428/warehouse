package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    @Column(unique = true, nullable = false)
    private String sku;             // 商品SKU（唯一）
    @Column(nullable = false)
    private String name;            // 商品名称
    private String category;        // 商品分类
    private Double price;           // 商品价格
    private Integer stock;          // 库存数量
}