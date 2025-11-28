package com.warehouse.warehouse_management.repository;

import com.warehouse.warehouse_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查询用户
    Optional<User> findByUsername(String username);
}