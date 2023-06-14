package com.taki.cloud.mail.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TakiMailProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 17:08
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.mail")
public class TakiMailProperties {

    /**
     * 是否开启mail功能
     */
    private boolean enable;

    /**
     * SMTP server host
     */
    private String host;

    /**
     * SMTP server的端口
     */
    private Integer port;

    /**
     * SMTP server的用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮件协议
     */
    private String protocol = "smtp";

    /**
     * 额外要增加的属性
     */
    private Map<String, String> properties = new HashMap<>();
}
