package com.warehouse.warehouse_management.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 主键ID

    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名最多 64 个字符")
    @Column(unique = true, nullable = false, length = 64)
    private String username;        // 用户名（唯一）

    /** BCrypt 哈希；WRITE_ONLY 保证它永远不会出现在接口响应里 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需在 6~64 位之间")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;        // 密码（BCrypt 加密存储）

    @Size(max = 64, message = "昵称最多 64 个字符")
    @Column(length = 64)
    private String nickname;        // 昵称

    @Size(max = 20, message = "手机号最多 20 个字符")
    @Column(length = 20)
    private String phone;           // 手机号
}
