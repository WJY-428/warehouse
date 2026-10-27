package com.warehouse.warehouse_management.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    LOGIN_ERROR(401, "用户名或密码错误"),
    NOT_FOUND(404, "资源不存在"),
    FAIL(500, "操作失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
