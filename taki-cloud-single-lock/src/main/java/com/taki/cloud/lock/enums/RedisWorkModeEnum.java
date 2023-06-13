package com.taki.cloud.lock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName RedisWorkMode
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 16:12
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum RedisWorkModeEnum {


    WORK_MODE_STANDALONE("redis-single","redis单节点部署"),
    WORK_MODE_SENTINEL("redis-sentinel","哨兵模式部署"),
    WORK_MODE_CLUSTER("redis-cluster","已集群模式部署"),
    WORK_MODE_MASTER_SLAVE("master-slave","主从模式部署");

    ;


    private final String workMode;

    private final  String desc;
}
