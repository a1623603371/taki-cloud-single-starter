package com.taki.cloud.lock.redisson;

import com.taki.cloud.lock.enums.RedissonEnum;
import com.taki.cloud.lock.properties.DistributedLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

/**
 * @ClassName ClusterRedissionConfig
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 16:31
 * @Version 1.0
 */
@Slf4j
public class ClusterRedissonConfig {

    public static Config createRedissonConfig(DistributedLockProperties properties){
        Config config  = new Config();

        try {
            String address = properties.getAddress();
            String password = properties.getPassword();
            String[] addrTokens = address.split(","); // 多节点，master，slave 节点
            // 设置 cluster 节点服务IP 和端口

            for (String addrToken : addrTokens) {
                config.useClusterServers().addNodeAddress(RedissonEnum.REDISSION_CONNECTION_PREFIX.getValue() + addrToken);

                if (!StringUtils.isEmpty(password)){
                    config.useClusterServers().setPassword(password);
                }
            }
            log.info("初始化【cluster】 方式 COnfig，redisAddress :{}",address );
        }catch (Exception e){
            log.error("cluster Redisson init error",e);
            e.printStackTrace();
        }

        return config;
    }
}
