package com.warehouse.warehouse_management.controller;

import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.entity.InboundRecord;
import com.warehouse.warehouse_management.service.InboundRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inbound")
@RequiredArgsConstructor
public class InboundRecordController {
    private final InboundRecordService inboundRecordService;

    // 查询所有入库记录
    @GetMapping("/list")
    public Result<List<InboundRecord>> findAll() {
        return Result.success(inboundRecordService.findAll());
    }

    // 新增入库记录
    @PostMapping("/save")
    public Result<InboundRecord> save(@RequestBody InboundRecord inboundRecord) {
        return Result.success(inboundRecordService.save(inboundRecord));
    }
}