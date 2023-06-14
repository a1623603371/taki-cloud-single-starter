package com.taki.cloud.lock.configuration;

import com.taki.cloud.lock.properties.DistributedLockProperties;
import com.taki.cloud.lock.redisson.SingleRedissonConfig;
import com.taki.cloud.lock.template.CuratorDistributedLockTemplate;
import com.taki.cloud.lock.template.DistributeLockTemplate;
import com.taki.cloud.lock.template.RedissonDistributedLockTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DistributedLockAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 16:47
 * @Version 1.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DistributedLockProperties.class)
public class DistributedLockAutoConfiguration {



    @Bean
    @ConditionalOnProperty(value = "taki.distributed.lock.redisWorkMode",havingValue = "standalone")
    public Config singleRedissonConfig(DistributedLockProperties properties){

        return SingleRedissonConfig.createRedisConfig(properties) ;

    }



    @Bean
    @ConditionalOnProperty(value = "taki.distributed.lock.redisWorkMode",havingValue = "master-slaves")
    public Config masterSlavesConfig(DistributedLockProperties properties){

        return SingleRedissonConfig.createRedisConfig(properties) ;

    }


    @Bean
    @ConditionalOnProperty(value = "taki.distributed.lock.redisWorkMode",havingValue = "sentinel")
    public Config sentinelRedissonConfig(DistributedLockProperties properties){

        return SingleRedissonConfig.createRedisConfig(properties) ;

    }


    @Bean
    @ConditionalOnProperty(value = "taki.distributed.lock.redisWorkMode",havingValue = "sentinel")
    public Config clusterRedissonConfig(DistributedLockProperties properties){

        return SingleRedissonConfig.createRedisConfig(properties) ;

    }


    @Bean
    @ConditionalOnClass(Redisson.class)
    @ConditionalOnProperty(value = "taki.distributed.lock.type", havingValue = "redis")
    public DistributeLockTemplate redisDistributedLockTemplate(Config config) {
        // redis基础操作的客户端，jedis，主要是提供的redis开源的操作
        // 对于企业级的缓存开发，redisson就可以了，他底层会基于jedis封装，开发和提供分布式锁在内的企业级的功能
        // 分布式锁的话，通常来说的话，都是通过redisson来实现的
        // zk也是有开源的框架，ZookeeperClient，我们可以使用企业级框架，curator，提供了很多企业级的功能
        // 之前的config装配，仅仅是针对单机、主从、哨兵、集群四种模式下，进行config里的redis地址设置
        // 我们真正构建redisson client的时候，针对不同的部署模式创建redisson client
        RedissonClient redissonClient = Redisson.create(config);
        // 对外封装的一套API就是我们的RedissonDistributedLockTemplate
        // 实现DistributedLockTemplate这个接口的
        return new RedissonDistributedLockTemplate(redissonClient); // redisson client封装DistributedLockTemplate
    }


    @Bean
    @ConditionalOnClass(CuratorFramework.class)
    @ConditionalOnProperty(value = "ruyuan.distributed.lock.type", havingValue = "zk")
    public DistributeLockTemplate curatorDistributedLockTemplate(DistributedLockProperties distributedLockProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                distributedLockProperties.getAddress(),
                retryPolicy);
        client.start();
        // 实现DistributedLockTemplate接口
        // 把redisson和curator两个框架分布式锁对外的API接口，通过我的接口进行统一
        return new CuratorDistributedLockTemplate(client);
    }
}
