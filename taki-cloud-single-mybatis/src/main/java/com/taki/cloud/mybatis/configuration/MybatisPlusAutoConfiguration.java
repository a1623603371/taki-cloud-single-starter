package com.taki.cloud.mybatis.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.taki.cloud.common.properties.YamlPropertyLoader;
import com.taki.cloud.mybatis.handler.TakiMetaObjectHandler;
import com.taki.cloud.mybatis.interceptor.SqlLogInterceptor;
import com.taki.cloud.mybatis.properties.TakiMybatisProperties;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @ClassName MybatisPlusAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/16 17:04
 * @Version 1.0
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@EnableConfigurationProperties(TakiMybatisProperties.class)
@PropertySource(factory = YamlPropertyLoader.class,value = "calsspath:taki-mybaits-puls.yml")
@MapperScan("com.taki.**.mapper.**")
public class MybatisPlusAutoConfiguration {
    
    /*** 
     * @description:  sql 日志拦截器
     * @param 
     * @return
     * @author Long
     * @date: 2023/6/16 17:18
     */ 
    @Bean
    @ConditionalOnProperty(value = "mybatis-plus.sql-log.enable",havingValue = "true",matchIfMissing = true)
    public SqlLogInterceptor sqlLogInterceptor() {
        return  new SqlLogInterceptor();
    }

    /**
     * mp相关插件的拦截器
     * @return mp插件
     */
    @Bean
    @ConditionalOnProperty(value = "ruyuan.mybatis.page-enable", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件: PaginationInnerInterceptor
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 防止全表更新与删除插件: BlockAttackInnerInterceptor
        BlockAttackInnerInterceptor blockAttackInnerInterceptor = new BlockAttackInnerInterceptor();
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor);
        return interceptor;
    }

    /**
     * 公共字段自动填充数据 如：创建时间 修改时间
     */
    @Bean
    @ConditionalOnProperty(value = "ruyuan.mybatis.auto-fill", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new TakiMetaObjectHandler();
    }
}
