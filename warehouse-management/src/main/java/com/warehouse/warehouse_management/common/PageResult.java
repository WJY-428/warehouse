package com.warehouse.warehouse_management.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页返回体。不直接返回 Spring 的 Page：它的 JSON 结构不稳定，
 * 且新版 Spring Boot 会对直接序列化 Page 给出警告。
 */
@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;   // 当前页数据
    private long total;     // 总条数
    private int page;       // 当前页（从 1 开始）
    private int size;       // 每页条数

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }
}
