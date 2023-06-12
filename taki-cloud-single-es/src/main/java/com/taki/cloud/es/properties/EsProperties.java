package com.taki.cloud.es.properties;

import com.tencentcloudapi.tci.v20190318.models.LightStandard;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName EsProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 16:42
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.es")
public class EsProperties {


    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 集群节点
     */
    private List<String> clusterNodes = new ArrayList<>(Collections.singletonList("localhost:9200"));


    private int connectionTimeoutMillis = 1000;


    private int socketTimeoutMillis = 30000;

    private int connectionRequestTimeoutMillis = 500;

    /**
     * 路由节点的最大数量连接数
     */
    private int maxConnPerRoute = 10;

    /**
     * clinet 最大连接数量
     */
    private int maxConnTotal = 30 ;


}
