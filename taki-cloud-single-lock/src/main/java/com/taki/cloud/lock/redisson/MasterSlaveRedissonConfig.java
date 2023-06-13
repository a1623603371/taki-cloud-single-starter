package com.taki.cloud.lock.redisson;

import com.taki.cloud.lock.enums.RedissonEnum;
import com.taki.cloud.lock.properties.DistributedLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MasterSlaveRedissonConfig
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 16:39
 * @Version 1.0
 */
@Slf4j
public class MasterSlaveRedissonConfig {


    public Config creteRedissonConfig(DistributedLockProperties  properties){
        Config config = new Config();

        try {
        String address = properties.getAddress();
        String password = properties.getPassword();
        int database = properties.getRedisDatabase();

        String[] addTokens = address.split(",");
        String masterNodeAddr = addTokens[0] ; // 切割以后第一个地址，就是master地址，第n个就是salve地址

            // 设置 主节点ip
        config.useMasterSlaveServers().setMasterAddress(masterNodeAddr);

        if (!StringUtils.isEmpty(password)){
            config.useMasterSlaveServers().setPassword(password);
        }

        config.useMasterSlaveServers().setDatabase(database);

        // 设置从节点，移除第一个节点，默认 第一个节点为主节点

            List<String > slaveList = new ArrayList<>();

            for (String addToken : addTokens) {
                slaveList.add(RedissonEnum.REDISSION_CONNECTION_PREFIX.getValue() + addToken);
            }

            slaveList.remove(0);
            config.useMasterSlaveServers().addSlaveAddress((String[]) slaveList.toArray());
            log.info("初始化【masterslave】方式Config redisAddress：{}" +  address);

        }catch (Exception e){
            log.error("masterslave Redisson init error",e);
            e.printStackTrace();
        }

        return config;
    }
}
