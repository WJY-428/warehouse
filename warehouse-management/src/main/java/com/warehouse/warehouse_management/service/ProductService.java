package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.entity.Product;
import com.warehouse.warehouse_management.repository.InboundRecordRepository;
import com.warehouse.warehouse_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final InboundRecordRepository inboundRecordRepository;

    // 查询所有商品
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 新增商品
    public Product save(Product product) {
        return productRepository.save(product);
    }

    // 根据ID删除商品
    public void deleteById(Long id) {
        // 先删除关联的入库记录
        inboundRecordRepository.findByProductId(id).forEach(inboundRecordRepository::delete);
        // 再删除商品
        productRepository.deleteById(id);
    }
}