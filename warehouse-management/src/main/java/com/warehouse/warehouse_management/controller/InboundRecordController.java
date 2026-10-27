package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.PageResult;
import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.dto.InboundRequest;
import com.warehouse.warehouse_management.entity.InboundRecord;
import com.warehouse.warehouse_management.service.InboundRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
public class InboundRecordController {

    private final InboundRecordService inboundRecordService;

    @GetMapping("/list")
    public Result<PageResult<InboundRecord>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(inboundRecordService.page(keyword, page, size));
    }

    /** 新增入库：写记录 + 累加库存（同一事务） */
    @PostMapping("/save")
    public Result<InboundRecord> save(@Valid @RequestBody InboundRequest request) {
        return Result.success(inboundRecordService.create(request));
    }

    /** 撤销入库：扣回库存 + 删记录 */
    @DeleteMapping("/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        inboundRecordService.cancel(id);
        return Result.success();
    }
}
