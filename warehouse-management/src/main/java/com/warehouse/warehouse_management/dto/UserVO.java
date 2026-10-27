package com.warehouse.warehouse_management.dto;

import com.warehouse.warehouse_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 对前端暴露的用户信息（不含密码） */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getPhone());
    }
}
