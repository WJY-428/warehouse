package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.common.BusinessException;
import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.ResultCode;
import com.warehouse.warehouse_management.entity.Product;
import com.warehouse.warehouse_management.repository.InboundRecordRepository;
import com.warehouse.warehouse_management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final InboundRecordRepository inboundRecordRepository;

    /** 分页 + 关键字搜索 */
    public PageResult<Product> page(String keyword, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<Product> result = StringUtils.hasText(keyword)
                ? productRepository.search(keyword.trim(), pageable)
                : productRepository.findAll(pageable);
        return PageResult.of(result);
    }

    /** 下拉框用：全部商品（不分页） */
    public List<Product> listAll() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
    }

    @Transactional
    public Product save(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new BusinessException("SKU 已存在：" + product.getSku());
        }
        product.setId(null);
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getPrice() == null) {
            product.setPrice(BigDecimal.ZERO);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product form) {
        Product existing = findById(id);
        if (!existing.getSku().equals(form.getSku()) && productRepository.existsBySku(form.getSku())) {
            throw new BusinessException("SKU 已存在：" + form.getSku());
        }
        existing.setSku(form.getSku());
        existing.setName(form.getName());
        existing.setCategory(form.getCategory());
        existing.setPrice(form.getPrice());
        existing.setStock(form.getStock() == null ? 0 : form.getStock());
        return productRepository.save(existing);
    }

    /** 删除商品：先清理关联入库记录，再删商品，整个过程一个事务 */
    @Transactional
    public void deleteById(Long id) {
        Product product = findById(id);
        inboundRecordRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    static Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 1) - 1;
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
    }
}
