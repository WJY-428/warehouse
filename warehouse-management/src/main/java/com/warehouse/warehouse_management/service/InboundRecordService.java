package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.common.BusinessException;
import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.ResultCode;
import com.warehouse.warehouse_management.dto.InboundRequest;
import com.warehouse.warehouse_management.entity.InboundRecord;
import com.warehouse.warehouse_management.entity.Product;
import com.warehouse.warehouse_management.entity.Warehouse;
import com.warehouse.warehouse_management.repository.InboundRecordRepository;
import com.warehouse.warehouse_management.repository.ProductRepository;
import com.warehouse.warehouse_management.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 入库业务。
 * 核心：入库记录与商品库存是两份数据，必须同成功同失败（@Transactional），
 * 否则会出现"记录写进去了但库存没加"的脏数据。
 */
@Service
@RequiredArgsConstructor
public class InboundRecordService {

    private final InboundRecordRepository inboundRecordRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public PageResult<InboundRecord> page(String keyword, int page, int size) {
        Pageable pageable = ProductService.buildPageable(page, size);
        Page<InboundRecord> result = StringUtils.hasText(keyword)
                ? inboundRecordRepository.search(keyword.trim(), pageable)
                : inboundRecordRepository.findAll(pageable);
        return PageResult.of(result);
    }

    /**
     * 新增入库：写记录 + 商品库存累加。
     *
     * @throws BusinessException 商品/仓库不存在
     */
    @Transactional
    public InboundRecord create(InboundRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("仓库不存在"));

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        product.setStock(currentStock + request.getQuantity());
        productRepository.save(product);

        InboundRecord record = new InboundRecord();
        record.setProduct(product);
        record.setWarehouse(warehouse);
        record.setQuantity(request.getQuantity());
        return inboundRecordRepository.save(record);
    }

    /**
     * 撤销入库（删除记录）：扣回对应库存。
     * 库存不够扣说明现在的库存已经被别的原因改小了，直接拒绝，避免出现负库存。
     */
    @Transactional
    public void cancel(Long id) {
        InboundRecord record = inboundRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "入库记录不存在"));

        Product product = record.getProduct();
        int currentStock = product.getStock() == null ? 0 : product.getStock();
        if (currentStock < record.getQuantity()) {
            throw new BusinessException("当前库存不足，无法撤销该入库记录");
        }
        product.setStock(currentStock - record.getQuantity());
        productRepository.save(product);

        inboundRecordRepository.delete(record);
    }
}
