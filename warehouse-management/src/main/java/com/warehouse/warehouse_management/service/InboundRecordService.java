package com.warehouse.warehouse_management.service;

import com.warehouse.warehouse_management.entity.InboundRecord;
import com.warehouse.warehouse_management.repository.InboundRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InboundRecordService {
    private final InboundRecordRepository inboundRecordRepository;

    // 查询所有入库记录
    public List<InboundRecord> findAll() {
        return inboundRecordRepository.findAll();
    }

    // 新增入库记录
    public InboundRecord save(InboundRecord inboundRecord) {
        return inboundRecordRepository.save(inboundRecord);
    }
}