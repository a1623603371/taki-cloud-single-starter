package com.taki.cloud.mq.consumer;

/**
 * @ClassName MessageListener
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 16:19
 * @Version 1.0
 */
public interface MessageListener {

    /**** 
     * @description:  处理消息
     * @param message 消息
     * @return  void
     * @author Long
     * @date: 2023/6/15 16:19
     */
    default void onMessage(String message){onMessage(message,()->{});};



    /***
     * @description: 处理消息
     * @param message 消息
     * @param  acknowledgement 提交offset ，处理消息一定要提交
     * @return  void
     * @author Long
     * @date: 2023/6/15 16:21
     */
    void onMessage(String message,Acknowledgement acknowledgement);
}
