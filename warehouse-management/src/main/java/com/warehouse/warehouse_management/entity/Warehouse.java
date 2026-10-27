package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID

    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 64, message = "仓库编码最多 64 个字符")
    @Column(unique = true, nullable = false, length = 64)
    private String code;            // 仓库编码（唯一）

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 128, message = "仓库名称最多 128 个字符")
    @Column(nullable = false, length = 128)
    private String name;            // 仓库名称

    @Size(max = 255, message = "地址最多 255 个字符")
    @Column(length = 255)
    private String address;         // 仓库地址

    @Size(max = 64, message = "管理员最多 64 个字符")
    @Column(length = 64)
    private String manager;         // 仓库管理员
}
