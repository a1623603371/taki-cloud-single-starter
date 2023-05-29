package com.taki.cloud.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName SysCommonExcetion
 * @Description TODO
 * @Author Long
 * @Date 2023/5/29 23:59
 * @Version 1.0
 */
@Getter
public class SysCommonException extends Exception {


    public SysCommonException() {
    }

    public SysCommonException(String message) {
        super(message);
    }

    public SysCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public SysCommonException(Throwable cause) {
        super(cause);
    }

    public SysCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
