package com.taki.cloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName BusinessError
 * @Description TODO
 * @Author Long
 * @Date 2023/5/29 23:45
 * @Version 1.0
 */
@AllArgsConstructor
@Getter
public enum BusinessError {

    SYS_UNKNOWN_ERROR(10001, "未知异常"),
    SYS_NO_OBJECT_FOUND(10002, "请求资源不存在"),
    SYS_NO_HANDLER_FOUND(10003, "未找到请求路径"),
    SYS_REQUEST_PARAMETER_ERROR(10004, "请求参数错误"),
    SYS_REQUEST_PARAMETER_TYPE_NOT_MATCH_ERROR(10005, "请求参数类型不匹配"),
    SYS_REQUEST_PARAMETER_INVALIDATE(10006, "参数效验失败"),
    SYS_REQUEST_PARAMETER_IS_NULL(10007, "请求参数不可以为空"),
    SYS_INTERNAL_SERVER_ERROR(10007, "系统内部错误"),
    SYS_CONTENT_CHECK_ERROR(10008, "系统检查错误"),
    SYS_NOT_FOUND_YAML_FILE(10009, "未找到对应的yaml资源文件");

    private Integer errCode;

    private String errMsg;
}
