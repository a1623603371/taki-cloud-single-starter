package com.taki.cloud.lock.redisson;

import com.taki.cloud.lock.enums.RedissonEnum;
import com.taki.cloud.lock.properties.DistributedLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

/**
 * @ClassName SentineRedissonConfig
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 17:35
 * @Version 1.0
 */
@Slf4j
public class SentinelRedissonConfig {

    public Config createRedissonConfig(DistributedLockProperties properties){

        Config config = new Config();

        try {
            String address = properties.getAddress();
            String password = properties.getPassword();
            int database = properties.getRedisDatabase();
            String[] addTokens = address.split(",");
            String sentinelAliasName = addTokens[0];

            // 设置redis配置文件sentinel.conf配置的sentinel别名
            // 哨兵模式之下，设置的集群节点，就是设置的是哨兵自己本身的集群节点地址
            // 哨兵自己为了保证自己是高可用的，他自己也可以是多节点来进行部署
            // 把哨兵别名，设置为了sentinel server的master name
            config.useSentinelServers().setMasterName(sentinelAliasName);
            config.useSentinelServers().setDatabase(database);

            if (!StringUtils.isEmpty(password)){
                config.useSentinelServers().setPassword(password);
            }

            for (int i = 1; i < addTokens.length; i++) {
                config.useSentinelServers().addSentinelAddress(RedissonEnum.REDISSION_CONNECTION_PREFIX.getValue() + addTokens[i]);
            }
            log.info("初始化【sentinel ]方式Confg，redisAddress：{}",address);

        }catch (Exception e){
            log.error("初始化【sentinel init error]{}",e);
            e.printStackTrace();

        }

        return config;
    }
}
