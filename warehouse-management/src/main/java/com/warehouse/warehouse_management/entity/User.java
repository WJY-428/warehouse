package com.warehouse.warehouse_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID
    @Column(unique = true, nullable = false)
    private String username;        // 用户名（唯一）
    @Column(nullable = false)
    private String password;        // 密码
    private String nickname;        // 昵称
    private String phone;           // 手机号
}