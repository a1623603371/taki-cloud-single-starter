package com.taki.cloud.excel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.beans.ConstructorProperties;

/**
 * @ClassName EsProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 19:42
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.easy.poi")
public class EsProperties {

    /**
     * 是否开启 pol 组件
     */
    private boolean enable;

    /**
     * 不存在默认创建 导入execl 时数据的存储路径
     */
    private String importExcelStorePath;

}
