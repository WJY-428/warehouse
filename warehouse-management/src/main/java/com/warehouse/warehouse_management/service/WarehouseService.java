package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.entity.Warehouse;
import com.warehouse.warehouse_management.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    // 查询所有仓库
    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    // 新增仓库
    public Warehouse save(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    // 根据ID查询仓库
    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("仓库不存在"));
    }
    
    // 编辑仓库
    public Warehouse update(Warehouse warehouse) {
        // 首先检查仓库是否存在
        Warehouse existingWarehouse = findById(warehouse.getId());
        
        // 更新仓库信息
        existingWarehouse.setCode(warehouse.getCode());
        existingWarehouse.setName(warehouse.getName());
        existingWarehouse.setAddress(warehouse.getAddress());
        existingWarehouse.setManager(warehouse.getManager());
        
        // 保存更新后的仓库
        return warehouseRepository.save(existingWarehouse);
    }
}