package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.Product;
import com.warehouse.warehouse_management.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 查询所有商品
    @GetMapping("/list")
    public Result<List<Product>> findAll() {
        return Result.success(productService.findAll());
    }

    // 新增商品
    @PostMapping("/save")
    public Result<Product> save(@RequestBody Product product) {
        return Result.success(productService.save(product));
    }

    // 删除商品
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return Result.success();
    }
}