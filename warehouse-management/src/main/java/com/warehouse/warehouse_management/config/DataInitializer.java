package com.warehouse.warehouse_management.config;

import com.warehouse.warehouse_management.entity.User;
import com.warehouse.warehouse_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时维护默认账号：
 * 1) 库里没有 admin → 创建 admin / 123456（BCrypt 加密）
 * 2) 库里已有明文密码的历史数据 → 就地升级为 BCrypt
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 兼容老数据：把明文密码统一换成 BCrypt 哈希
        userRepository.findAll().stream()
                .filter(user -> user.getPassword() != null && !user.getPassword().startsWith("$2"))
                .forEach(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                    log.warn("检测到明文密码，已升级为 BCrypt：{}", user.getUsername());
                });

        if (userRepository.findByUsername(DEFAULT_USERNAME).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setUsername(DEFAULT_USERNAME);
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setNickname("系统管理员");
        admin.setPhone("");
        userRepository.save(admin);
        log.warn("未检测到默认账号，已创建：{} / {}（请尽快修改密码）", DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}
