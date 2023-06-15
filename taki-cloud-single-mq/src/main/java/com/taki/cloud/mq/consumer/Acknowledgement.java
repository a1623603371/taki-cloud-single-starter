package com.taki.cloud.mq.consumer;

/**
 * @ClassName Acknowledgement
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 16:20
 * @Version 1.0
 */
public interface Acknowledgement {

    /**
     * 提交 offset
     */
    void ack();
}
