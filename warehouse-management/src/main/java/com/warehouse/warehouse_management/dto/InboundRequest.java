package com.warehouse.warehouse_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 入库请求参数。
 * 直接收实体的话，前端要传 {"product":{"id":1}} 这种嵌套结构，
 * 既不直观也容易被塞进额外字段；用 DTO 只暴露需要的参数。
 */
@Data
public class InboundRequest {
    @NotNull(message = "请选择商品")
    private Long productId;

    @NotNull(message = "请选择仓库")
    private Long warehouseId;

    @NotNull(message = "请输入入库数量")
    @Min(value = 1, message = "入库数量必须大于 0")
    private Integer quantity;
}
