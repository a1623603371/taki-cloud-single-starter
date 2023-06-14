package com.taki.cloud.lock.template;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CuratorDisteributedLockTemplate
 * @Description TODO
 * @Author Long
 * @Date 2023/6/14 16:40
 * @Version 1.0
 */
@AllArgsConstructor
@Slf4j
public class CuratorDistributedLockTemplate implements DistributeLockTemplate{

    private CuratorFramework curatorFramework;

    @Override
    public DistributedLock getLock(String name) {

        if (name.startsWith("/")){
            throw new IllegalArgumentException("zk加锁名称不合法");
        }

        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework,name);

        return new DistributedLock() {
            @Override
            public Boolean lock() throws Exception {
                interProcessMutex.acquire();
                return true;
            }

            @Override
            public Boolean lock(int time, TimeUnit timeUnit) throws Exception {
                return interProcessMutex.acquire(time,timeUnit);
            }

            @Override
            public Boolean unlock() {

                try {
                    if (interProcessMutex.isOwnedByCurrentThread()){
                        interProcessMutex.release();
                        return true;
                    }
                }catch (Exception e) {
                    log.error("Distributed lock unlock failed. ", e);
                    e.printStackTrace();
                }

              return false;
            }
        };
    }
}
