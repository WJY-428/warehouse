package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    @Column(unique = true, nullable = false)
    private String code;            // 仓库编码（唯一）
    @Column(nullable = false)
    private String name;            // 仓库名称
    private String address;         // 仓库地址
    private String manager;         // 仓库管理员
}