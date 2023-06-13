package com.taki.cloud.excel.configuration;

import com.taki.cloud.excel.properties.EsProperties;
import com.taki.cloud.excel.template.ExcelTemplate;
import com.taki.cloud.excel.template.ExcelTemplateImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.io.File;

/**
 * @ClassName EasyPoiAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 14:48
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(EsProperties.class)
@ConditionalOnProperty(prefix = "taki,easy.poi",name = "enable",matchIfMissing = false)
@ComponentScan(basePackages = {"cn.afterturn.easypoi"}) // 扫描 easypoi 下的bean
public class EasyPoiAutoConfiguration {

    @Autowired
    private EsProperties esProperties;


    @Bean
    @ConditionalOnMissingBean
    public BeanNameViewResolver beanNameViewResolver(){
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(10);

        return resolver;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcelTemplate excelTemplate (){

        doPropertiesCheck(esProperties);

        return new ExcelTemplateImpl(esProperties.getImportExcelStorePath());

    }

    private void doPropertiesCheck(EsProperties esProperties) {

        // 导入excel时数据的存储路径
        String importExcelStorePath = esProperties.getImportExcelStorePath();

        if (StringUtils.isEmpty(importExcelStorePath)){
        throw new IllegalArgumentException("请设置easy-poi的存储路径，配置项 为 ");
        }
        final File dir = new File(importExcelStorePath);
        // 如果 指定 的 RocksDB的存储目录不存在，则进行创建

        if (!dir.exists()){
            boolean mkdirsResult = dir.mkdirs();
            if (!mkdirsResult){
                throw new IllegalStateException("设置easy-poi的存储路径失败，创建easy-poi存储文件失败：" +  importExcelStorePath +"请检查是否存储在目录权限问题");
            }
        }

        // 如果指定的路径是否文件夹，而是文件
        if (!dir.isDirectory()){
            throw new IllegalStateException("设置easy-poi的存储路径失败，请指定文件夹而非文件：" + importExcelStorePath);
        }
    }
}
