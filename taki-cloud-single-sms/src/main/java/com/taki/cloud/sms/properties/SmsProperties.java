package com.taki.cloud.sms.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName SmsProperites
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:15
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.sms")
public class SmsProperties {


    /**
     * 大小类型
     */
    private String type;


    /**
     * 模板id
     */
    private String templateId;


    /**
     * accessKey
     */
    private String accessKey;


    /**
     *
     */
    private String secretKey;


    /**
     * 短信签名
     */
    private String signName;


    /**
     * 短信云特定参数 ：SDKAPPID
     */
    private String sdkAppId;

}
