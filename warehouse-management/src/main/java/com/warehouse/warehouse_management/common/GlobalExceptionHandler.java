package com.warehouse.warehouse_management.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;

/**
 * 全局异常处理：把各类异常翻译成统一的 Result。
 * 注意：只有"可预期的业务失败"才把 message 返回给前端，
 * 未知异常统一返回兜底文案，避免泄漏堆栈和 SQL 细节。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常（SKU 重复、库存不足、商品不存在……） */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {
        log.info("业务异常：{}", e.getMessage());
        return Result.custom(e.getCode(), e.getMessage());
    }

    /** @RequestBody + @Valid 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return Result.custom(ResultCode.PARAM_ERROR.getCode(), firstFieldMessage(e.getBindingResult().getFieldErrors()));
    }

    /** 表单/查询参数绑定失败 */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        return Result.custom(ResultCode.PARAM_ERROR.getCode(), firstFieldMessage(e.getBindingResult().getFieldErrors()));
    }

    /** 请求体不是合法 JSON、或字段类型对不上 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleNotReadable(HttpMessageNotReadableException e) {
        log.info("请求体解析失败：{}", e.getMessage());
        return Result.custom(ResultCode.PARAM_ERROR.getCode(), "请求体格式错误");
    }

    /** 唯一约束等数据库约束冲突 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("数据约束冲突：{}", e.getMostSpecificCause().getMessage());
        return Result.custom(ResultCode.PARAM_ERROR.getCode(), "数据冲突：可能存在重复的编码或外键引用");
    }

    /** 接口/静态资源不存在 */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> handleNotFound(NoResourceFoundException e) {
        return Result.custom(ResultCode.NOT_FOUND.getCode(), "接口不存在：" + e.getResourcePath());
    }

    /** 兜底：未知异常只记日志，不回传内部细节 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail("服务器内部错误，请稍后重试");
    }

    private String firstFieldMessage(java.util.List<FieldError> fieldErrors) {
        return Optional.ofNullable(fieldErrors)
                .flatMap(errors -> errors.stream().findFirst())
                .map(FieldError::getDefaultMessage)
                .orElse(ResultCode.PARAM_ERROR.getMessage());
    }
}
