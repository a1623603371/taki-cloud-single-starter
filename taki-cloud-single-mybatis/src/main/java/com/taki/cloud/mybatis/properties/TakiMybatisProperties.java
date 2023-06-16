package com.taki.cloud.mybatis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @ClassName TakiMybatisProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/16 14:18
 * @Version 1.0
 */
@Data
@RefreshScope
@ConfigurationProperties("taki.mybatis")
public class TakiMybatisProperties {

    /***
     * 是否打印可执行 sql
     */
    private     boolean sql  = true;


    /**
     * 是否开启分页插件
     */
    private boolean pageEnable = true;

    /**
     *是否开启公共字段自动填充逻辑
     */
    private boolean autoFill = true;


}

