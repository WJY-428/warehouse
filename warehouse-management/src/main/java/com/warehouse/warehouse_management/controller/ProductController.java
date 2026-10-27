package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.Product;
import com.warehouse.warehouse_management.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** 分页查询，keyword 可选 */
    @GetMapping("/list")
    public Result<PageResult<Product>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productService.page(keyword, page, size));
    }

    /** 入库页下拉框：全部商品 */
    @GetMapping("/options")
    public Result<List<Product>> options() {
        return Result.success(productService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.findById(id));
    }

    @PostMapping("/save")
    public Result<Product> save(@Valid @RequestBody Product product) {
        return Result.success(productService.save(product));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return Result.success(productService.update(id, product));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteById(id);
        return Result.success();
    }
}
