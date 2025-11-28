package com.warehouse.warehouse_management.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;       // 响应码
    private String message;    // 响应信息
    private T data;            // 响应数据

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    // 自定义响应
    public static <T> Result<T> error(ResultCode code) {
        Result<T> result = new Result<>();
        result.setCode(code.getCode());
        result.setMessage(code.getMessage());
        result.setData(null);
        return result;
    }
}