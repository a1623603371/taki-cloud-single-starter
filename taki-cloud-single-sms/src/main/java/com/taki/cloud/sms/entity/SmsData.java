package com.taki.cloud.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName SmsData
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:12
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsData  implements Serializable {


    private static final long serialVersionUID = 40964136168331492L;

    /**
     * 参数列表
     */
    private Map<String,String> params;
}
