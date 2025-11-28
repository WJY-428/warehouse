package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.Warehouse;
import com.warehouse.warehouse_management.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    // 查询所有仓库
    @GetMapping("/list")
    public Result<List<Warehouse>> findAll() {
        return Result.success(warehouseService.findAll());
    }

    // 新增仓库
    @PostMapping("/save")
    public Result<Warehouse> save(@RequestBody Warehouse warehouse) {
        return Result.success(warehouseService.save(warehouse));
    }

    // 根据ID查询仓库
    @GetMapping("/{id}")
    public Result<Warehouse> findById(@PathVariable Long id) {
        return Result.success(warehouseService.findById(id));
    }
    
    // 编辑仓库
    @PutMapping("/update")
    public Result<Warehouse> update(@RequestBody Warehouse warehouse) {
        return Result.success(warehouseService.update(warehouse));
    }
}