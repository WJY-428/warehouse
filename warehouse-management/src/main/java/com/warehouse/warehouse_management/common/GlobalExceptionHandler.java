package com.warehouse.warehouse_management.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice  // 全局异常处理
public class GlobalExceptionHandler {

    // 参数校验异常
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        log.error("参数校验失败：", e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(ResultCode.PARAM_ERROR);
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.fail(e.getMessage());
    }
}