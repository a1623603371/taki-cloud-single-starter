package com.taki.cloud.common.properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName YamlPropertyLoader
 * @Description 加载yml格式的自定义配置文件
 * @Author Long
 * @Date 2023/5/30 0:00
 * @Version 1.0
 */
public class YamlPropertyLoader  implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {

        Properties propertiesFromYaml = loadYaml2Properties(encodedResource);
        String sourceName = name !=null ? name : encodedResource.getResource().getFilename();
        assert sourceName !=null;
        return new PropertiesPropertySource(sourceName,propertiesFromYaml);
    }



    /*** 
     * @description:  加载yml文件将 yaml文件变成 properties 对象
     * @param resource yaml 资源文件对象
     * @return  java.util.Properties
     * @author Long
     * @date: 2023/5/30 0:03
     */ 
    private Properties loadYaml2Properties(EncodedResource resource) throws FileNotFoundException {

        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        }catch (IllegalStateException e){
            Throwable cause = e.getCause();
            if (cause instanceof   FileNotFoundException ){
                throw e;
            }

            throw e;
        }




    }
}
