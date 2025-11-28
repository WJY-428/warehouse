package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.common.ResultCode;
import com.warehouse.warehouse_management.entity.User;
import com.warehouse.warehouse_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 用户登录
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ResultCode.LOGIN_ERROR.getMessage()));
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException(ResultCode.LOGIN_ERROR.getMessage());
        }
        return user;
    }

    // 新增用户
    public User save(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }
}