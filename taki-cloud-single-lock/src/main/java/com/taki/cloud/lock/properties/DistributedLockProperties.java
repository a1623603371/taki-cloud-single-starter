package com.taki.cloud.lock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName DistributedLockProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 15:55
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.distribute.lock")
public class DistributedLockProperties {

    /**
     * 分布式 锁类型， redis/zk
     */
    private String type;


    /**
     * server 地址： ip:prot , 有多个斗角号分割
     */
    private String address;


    /**
     * 密码
     */
    private String password;

    /***
     * redis server 的工作模式 ： stancelone ，sentinel，cluster，master-slave
     *
     */
    private String redisWordMode;

    /**
     * 连接数据库
     */
    private int redisDatabase;
}
