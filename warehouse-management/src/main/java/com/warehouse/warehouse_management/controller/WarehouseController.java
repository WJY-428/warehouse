package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.Warehouse;
import com.warehouse.warehouse_management.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/list")
    public Result<PageResult<Warehouse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(warehouseService.page(keyword, page, size));
    }

    /** 入库页下拉框：全部仓库 */
    @GetMapping("/options")
    public Result<List<Warehouse>> options() {
        return Result.success(warehouseService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Warehouse> detail(@PathVariable Long id) {
        return Result.success(warehouseService.findById(id));
    }

    @PostMapping("/save")
    public Result<Warehouse> save(@Valid @RequestBody Warehouse warehouse) {
        return Result.success(warehouseService.save(warehouse));
    }

    @PutMapping("/{id}")
    public Result<Warehouse> update(@PathVariable Long id, @Valid @RequestBody Warehouse warehouse) {
        return Result.success(warehouseService.update(id, warehouse));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return Result.success();
    }
}
