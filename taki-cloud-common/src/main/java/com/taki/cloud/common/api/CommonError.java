package com.taki.cloud.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName CommError
 * @Description 错误封装
 * @Author Long
 * @Date 2023/5/29 20:55
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonError implements Serializable {

    /**
     * 错误码
     */
    private Integer errorCode;


    /**
     * 错误描述
     */
    private String errorMsg;
}
