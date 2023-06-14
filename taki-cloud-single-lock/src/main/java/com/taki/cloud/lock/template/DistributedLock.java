package com.taki.cloud.lock.template;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName DistributedLock
 * @Description 分布式锁 接口
 * @Author Long
 * @Date 2023/6/14 16:16
 * @Version 1.0
 */
public interface DistributedLock {


    /*** 
     * @description:  阻塞 锁
     * @param 
     * @return  java.lang.Boolean
     * @author Long
     * @date: 2023/6/14 16:16
     */ 
    Boolean lock() throws Exception;


    /***
     * @description: 超时锁
     * @param time 时间
     * @param timeUnit  单位
     * @return  java.lang.Boolean
     * @author Long
     * @date: 2023/6/14 16:17
     */
    Boolean lock(int time, TimeUnit  timeUnit) throws Exception;

    /*** 
     * @description: 释放锁
     * @param 
     * @return  java.lang.Boolean
     * @author Long
     * @date: 2023/6/14 16:37
     */ 
    Boolean unlock();
}
