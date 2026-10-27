package com.warehouse.warehouse_management.common;

import lombok.Getter;

/**
 * 业务异常：携带错误码，由 GlobalExceptionHandler 统一转成 Result。
 * 用异常表达"可预期的业务失败"（如 SKU 重复、库存不足），
 * 避免在 service 里到处 if/else 拼返回值。
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        this(ResultCode.FAIL.getCode(), message);
        // custom message overrides enum message
    }

    public BusinessException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
