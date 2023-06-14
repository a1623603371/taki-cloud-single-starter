package com.taki.cloud.lock.template;

import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedissonDistrbuteLock
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 16:36
 * @Version 1.0
 */
@AllArgsConstructor
public class RedissonDistributedLockTemplate implements DistributeLockTemplate {

    private RedissonClient redissonClient;

    @Override
    public DistributedLock getLock(String name) {

        RLock rLock = redissonClient.getLock(name);


        return new DistributedLock() {
            @Override
            public Boolean lock() throws Exception {
                return rLock.tryLock();
            }

            @Override
            public Boolean lock(int time, TimeUnit timeUnit) throws Exception {
                return rLock.tryLock(time,timeUnit);
            }

            @Override
            public Boolean unlock() {

                if (rLock.isHeldByCurrentThread()){
                     rLock.unlock();
                     return  true;
                }
                return false;

            }
        };
    }
}
