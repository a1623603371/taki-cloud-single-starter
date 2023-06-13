package com.taki.cloud.lock.redisson;

import com.taki.cloud.lock.enums.RedissonEnum;
import com.taki.cloud.lock.properties.DistributedLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

/**
 * @ClassName SingleRedissonConfig
 * @Description 单节点 redisson
 * @Author Long
 * @Date 2023/6/13 18:52
 * @Version 1.0
 */
@Slf4j
public class SingleRedissonConfig {



        public static Config createRedisConfig(DistributedLockProperties properties){
        Config config = new Config();

        try {

            String address = properties.getAddress();
            String password = properties.getPassword();
            int database = properties.getRedisDatabase();
            String redisAddr = RedissonEnum.REDISSION_CONNECTION_PREFIX.getValue() + address;
            config.useSingleServer().setAddress(redisAddr);

            config.useSingleServer().setDatabase(database);
            if (!StringUtils.isEmpty(password)){
                config.useSingleServer().setPassword(password);
            }

            log.info("初始化方式Config，mode=stancedalone redisAddress={}",address);


        }catch (Exception e){
            log.error("standalone redisson init error,{}",e);
            e.printStackTrace();
        }

        return config;
        }
}
