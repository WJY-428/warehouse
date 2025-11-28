package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.User;
import com.warehouse.warehouse_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 用户登录
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
    String username = params.get("username");
    String password = params.get("password");
    User user = userService.login(username, password);
    return Result.success(user);
}

    // 新增用户
    @PostMapping("/save")
    public Result<User> save(@RequestBody User user) {
        return Result.success(userService.save(user));
    }

    // 用户退出（前端清除token即可，后端无需处理）
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success();
    }
}