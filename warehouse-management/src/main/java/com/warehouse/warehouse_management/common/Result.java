package com.warehouse.warehouse_management.common;

import lombok.Data;

/**
 * 统一响应体：code=200 表示成功，其余为业务/系统错误码。
 */
@Data
public class Result<T> {
    private Integer code;       // 响应码
    private String message;     // 响应信息
    private T data;             // 响应数据

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    /** 业务失败：默认 500，可传入更精确的错误码与提示 */
    public static <T> Result<T> fail(String message) {
        return build(ResultCode.FAIL.getCode(), message, null);
    }

    public static <T> Result<T> custom(Integer code, String message) {
        return build(code, message, null);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return build(resultCode.getCode(), resultCode.getMessage(), null);
    }

    private static <T> Result<T> build(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
