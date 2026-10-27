package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.common.BusinessException;
import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.ResultCode;
import com.warehouse.warehouse_management.entity.Warehouse;
import com.warehouse.warehouse_management.repository.InboundRecordRepository;
import com.warehouse.warehouse_management.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final InboundRecordRepository inboundRecordRepository;

    public PageResult<Warehouse> page(String keyword, int page, int size) {
        Pageable pageable = ProductService.buildPageable(page, size);
        Page<Warehouse> result = StringUtils.hasText(keyword)
                ? warehouseRepository.search(keyword.trim(), pageable)
                : warehouseRepository.findAll(pageable);
        return PageResult.of(result);
    }

    /** 下拉框用：全部仓库（不分页） */
    public List<Warehouse> listAll() {
        return warehouseRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "仓库不存在"));
    }

    @Transactional
    public Warehouse save(Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new BusinessException("仓库编码已存在：" + warehouse.getCode());
        }
        warehouse.setId(null);
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Warehouse update(Long id, Warehouse form) {
        Warehouse existing = findById(id);
        if (!existing.getCode().equals(form.getCode()) && warehouseRepository.existsByCode(form.getCode())) {
            throw new BusinessException("仓库编码已存在：" + form.getCode());
        }
        existing.setCode(form.getCode());
        existing.setName(form.getName());
        existing.setAddress(form.getAddress());
        existing.setManager(form.getManager());
        return warehouseRepository.save(existing);
    }

    /** 删除仓库：有库存在里面就不允许删，避免入库记录变成孤儿数据 */
    @Transactional
    public void deleteById(Long id) {
        Warehouse warehouse = findById(id);
        if (inboundRecordRepository.existsByWarehouse_Id(id)) {
            throw new BusinessException("该仓库已有入库记录，不能删除");
        }
        warehouseRepository.delete(warehouse);
    }
}
