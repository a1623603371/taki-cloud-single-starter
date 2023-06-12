package com.taki.cloud.es.configuration;

import com.taki.cloud.es.properties.EsProperties;
import com.taki.cloud.es.template.EsTemplate;
import com.taki.cloud.es.template.EsTemplateImpl;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ElasticsearchAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/12 16:42
 * @Version 1.0
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnProperty(value = "taki.es.enable",havingValue = "true")
@EnableConfigurationProperties(EsProperties.class)
public class ElasticsearchAutoConfiguration {

    /*** 
     * @description:  初始化es 操作模板
     * @param esProperties es 属性
     * @return  com.taki.cloud.es.template.EsTemplate
     * @author Long
     * @date: 2023/6/12 19:22
     */ 

    @Bean
    public EsTemplate esTemplate(EsProperties esProperties){

        return new EsTemplateImpl(elasticsearchClient(esProperties));
    }

    /*** 
     * @description:  es 客户端
     * @param esProperties
     * @return  org.elasticsearch.client.RestHighLevelClient
     * @author Long
     * @date: 2023/6/12 19:25
     */ 
    @Bean
    public RestHighLevelClient elasticsearchClient(EsProperties  esProperties){
        // 创建restClinet的构造器
        RestClientBuilder restClientBuilder = RestClient.builder(loadHttpHosts(esProperties));

        // 设置连接超时时间等参数
        setConnectTimeOutConfig(restClientBuilder,esProperties);

        //设置http相关的连接的参数
        setHttpClientConfig(restClientBuilder,esProperties);

        return new RestHighLevelClient(restClientBuilder);

    }
    
    /*** 
     * @description:
     * @param restClientBuilder
     * @param  esProperties
     * @return  void
     * @author Long
     * @date: 2023/6/12 19:34
     */ 
    private void setHttpClientConfig(RestClientBuilder restClientBuilder, EsProperties esProperties) {

        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder->{
            httpClientBuilder.setMaxConnTotal(esProperties.getMaxConnTotal());
            httpClientBuilder.setMaxConnPerRoute(esProperties.getMaxConnPerRoute());
            return httpClientBuilder;
        });

    }

    /*** 
     * @description: 配置连接超时时间等参数
     * @param restClientBuilder 创建restClinet的构造器
     * @param esProperties es 参数配置
     * @return  void
     * @author Long
     * @date: 2023/6/12 19:31
     */ 
    private void setConnectTimeOutConfig(RestClientBuilder restClientBuilder, EsProperties esProperties) {

        restClientBuilder.setRequestConfigCallback(requestConfigBuilder->{
        requestConfigBuilder.setConnectTimeout(esProperties.getConnectionTimeoutMillis());
        requestConfigBuilder.setSocketTimeout(esProperties.getSocketTimeoutMillis());
        requestConfigBuilder.setConnectionRequestTimeout(esProperties.getConnectionRequestTimeoutMillis());

        return requestConfigBuilder;
        });

    }


    /*** 
     * @description:  加载 es  集群节点 ，逗号分割
     * @param esProperties
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/12 19:26
     */ 
    private HttpHost[] loadHttpHosts(EsProperties esProperties) {
        int size = esProperties.getClusterNodes().size();
        HttpHost[] httpHosts = new HttpHost[size];

        for (int i = 0; i < size; i++) {
            String clusterNode = esProperties.getClusterNodes().get(i);
            String[] hostAndPost = clusterNode.split(":");
            httpHosts[i] = new HttpHost(hostAndPost[0],Integer.parseInt(hostAndPost[1]));
        }

        return httpHosts;

    }
}
