package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.dto.LoginRequest;
import com.warehouse.warehouse_management.dto.LoginResponse;
import com.warehouse.warehouse_management.dto.UserVO;
import com.warehouse.warehouse_management.entity.User;
import com.warehouse.warehouse_management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 登录：返回 JWT + 脱敏后的用户信息 */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request.getUsername(), request.getPassword()));
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody User user) {
        return Result.success(userService.register(user));
    }

    /** 当前登录人信息（由 AuthInterceptor 放进 request 的 userId） */
    @GetMapping("/me")
    public Result<UserVO> me(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return Result.success(new UserVO((Long) userId, (String) request.getAttribute("username"), null, null));
    }
}
