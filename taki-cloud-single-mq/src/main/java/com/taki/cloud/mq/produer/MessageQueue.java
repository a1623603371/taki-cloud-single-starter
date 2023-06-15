package com.taki.cloud.mq.produer;

import com.taki.cloud.mq.exception.MessageQueueException;

/**
 * @ClassName MessageQueue
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 19:20
 * @Version 1.0
 */
public interface MessageQueue {

    /***
     * @description:  发送消息
     * @param topic
     * @param   message
     * @return  void
     * @author Long
     * @date: 2023/6/15 19:24
     */
    void send(String topic,String message) throws MessageQueueException;

    /***
     * @description:  发送消息
     * @param topic
     * @param   message
     * @return  void
     * @author Long
     * @date: 2023/6/15 19:24
     */
    void send(String topic,String message,String tags) throws MessageQueueException;

    /***
     * @description:  发送消息
     * @param topic
     * @param   message
     * @return  void
     * @author Long
     * @date: 2023/6/15 19:24
     */
    void send(String topic,String message,String tags,String key) throws MessageQueueException;


    /***
     * @description:  发送消息
     * @param topic
     * @param   message
     * @return  void
     * @author Long
     * @date: 2023/6/15 19:24
     */
    void send(String topic,String message,String tags,String key,Integer delayTimeLevel) throws MessageQueueException;


}
