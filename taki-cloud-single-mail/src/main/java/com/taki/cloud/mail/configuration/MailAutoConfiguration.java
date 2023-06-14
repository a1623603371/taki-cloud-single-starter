package com.taki.cloud.mail.configuration;

import com.taki.cloud.mail.properties.TakiMailProperties;
import com.taki.cloud.mail.template.MailTemplate;
import com.taki.cloud.mail.template.MailTemplateImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName MailAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 18:56
 * @Version 1.0
 */
@Configuration
@ConditionalOnProperty(value = "taki.mail.enable",havingValue = "true")
@EnableConfigurationProperties(TakiMailProperties.class)
public class MailAutoConfiguration {



    @Bean
    @ConditionalOnClass(MailTemplate.class)
    public MailTemplate mailTemplate (TakiMailProperties properties){

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        MailProperties mailProperties = mailProperties(properties);
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setProtocol(mailProperties.getProtocol());
        mailSender.setJavaMailProperties(asProperties(properties.getProperties()));
        
        
        return  new MailTemplateImpl(mailSender,mailProperties);

    }

    private Properties asProperties(Map<String, String> properties) {

        Properties mailProperties = new Properties();
        mailProperties.putAll(properties);

        return mailProperties;

    }

    @Bean
    @ConditionalOnClass(MailTemplate.class)
    public MailProperties mailProperties(TakiMailProperties properties) {

        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost(properties.getHost());
        mailProperties.setPort(properties.getPort());
        mailProperties.setUsername(properties.getUsername());
        mailProperties.setPassword(properties.getPassword());
        mailProperties.setProtocol(properties.getProtocol());
        mailProperties.setDefaultEncoding(StandardCharsets.UTF_8);

        return mailProperties;
    }

}
