package com.taki.cloud.sms.configuration;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.taki.cloud.sms.properties.SmsProperties;
import com.taki.cloud.sms.template.AliSmsTemplate;
import com.taki.cloud.sms.template.SmsTemplate;
import com.taki.cloud.sms.template.TencentSmsTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SmsAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 19:47
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {


    @Bean
    @ConditionalOnClass(Client.class)
    @ConditionalOnProperty(value = "taki.sms.type",havingValue = "aliyun")
    public SmsTemplate aliSmsTemplate(SmsProperties properties) throws Exception {

        Config config = new Config();
        config.setAccessKeyId(properties.getAccessKey());
        config.setAccessKeySecret(properties.getSecretKey());

        config.endpoint = "dysmsapi.aliyuncs.com";

        Client client = new Client(config);
        return new AliSmsTemplate(properties,client);

    }


    @Bean
    @ConditionalOnClass(Client.class)
    @ConditionalOnProperty(value = "taki.sms.type",havingValue = "tencent")
    public SmsTemplate tencentTemplate(SmsProperties properties) throws Exception {

        return new TencentSmsTemplate(properties);

    }
}
