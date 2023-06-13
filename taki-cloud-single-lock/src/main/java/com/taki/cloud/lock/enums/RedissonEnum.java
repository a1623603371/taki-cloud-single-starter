package com.taki.cloud.lock.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @ClassName RedissionEnums
 * @Description TODO
 * @Author Long
 * @Date 2023/6/13 16:08
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum RedissonEnum {

        REDISSION_CONNECTION_PREFIX("redis://","Redission地址前缀，配置redission客户端时使用");


        private final   String value;

        private final   String desc;
}
