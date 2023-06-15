package com.taki.cloud.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName SmsResponse
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:13
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsResponse  implements Serializable {


    private static final long serialVersionUID = 7564176887314034003L;


    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 状态码
     */
    private  String code;


    /**
     * 响应内容
     */
    private String msg;
}
