package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.common.BusinessException;
import com.warehouse.warehouse_management.common.ResultCode;
import com.warehouse.warehouse_management.dto.LoginResponse;
import com.warehouse.warehouse_management.dto.UserVO;
import com.warehouse.warehouse_management.entity.User;
import com.warehouse.warehouse_management.repository.UserRepository;
import com.warehouse.warehouse_management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 登录：用 BCrypt 校验密码，成功后签发 JWT。
     * 用户名不存在与密码错误返回同一提示，避免被用来枚举账号。
     */
    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.LOGIN_ERROR));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        String token = jwtUtil.generate(user.getId(), user.getUsername());
        return new LoginResponse(token, UserVO.from(user));
    }

    /** 注册：密码入库前先 BCrypt 加密 */
    @Transactional
    public UserVO register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return UserVO.from(userRepository.save(user));
    }
}
