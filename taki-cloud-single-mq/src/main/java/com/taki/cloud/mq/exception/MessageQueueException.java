package com.taki.cloud.mq.exception;

/**
 * @ClassName MessageQueueException
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 19:22
 * @Version 1.0
 */
public class MessageQueueException  extends Exception{

    public MessageQueueException() {
    }

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException(Exception e) {
        super(e);
    }
}
