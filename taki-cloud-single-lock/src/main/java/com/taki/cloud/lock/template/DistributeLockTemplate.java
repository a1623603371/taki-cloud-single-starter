package com.taki.cloud.lock.template;

/**
 * @ClassName DistributeLockTemplate
 * @Description 分布式锁 模板
 * @Author Long
 * @Date 2023/6/14 16:35
 * @Version 1.0
 */
public interface DistributeLockTemplate {

    /*** 
     * @description:  获取一个分布式锁
     * @param name
     * @return  com.taki.cloud.lock.template.DistributedLock
     * @author Long
     * @date: 2023/6/14 16:56
     */ 
    DistributedLock getLock(String name);
}
