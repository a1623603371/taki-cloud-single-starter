package com.taki.cloud.mq.produer;

import com.taki.cloud.mq.exception.MessageQueueException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName RocketMessageQueue
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 19:54
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class RocketMessageQueue implements  MessageQueue{

    private DefaultMQProducer producer;

    @Override
    public void send(String topic, String message) throws MessageQueueException {
        send(topic,message,null);
    }

    @Override
    public void send(String topic, String message, String tags) throws MessageQueueException {
        send(topic,message,tags,null);
    }

    @Override
    public void send(String topic, String message, String tags, String key) throws MessageQueueException {
        send(topic,message,tags,key,-1);
    }

    @Override
    public void send(String topic, String message, String tags, String key, Integer delayTimeLevel) throws MessageQueueException {

        // rocketmq，topic，很多的queue = kafka里面的partition的概念
        // key也可以决定一条消息如何路由 到一个queue里去，queue里是保持顺序性
        // tags，给一条数据打多个标签，后续我们在进行consumer消费的时候，可以消费指定的tags的消息
        // 可以在kafka/rocketmq，broker端，服务端，针对tags进行消息的过滤

        Message msg = new Message(topic,tags,key,message.getBytes(StandardCharsets.UTF_8));

        try {
            if (delayTimeLevel  > 0){
                msg.setDelayTimeLevel(delayTimeLevel);
            }

            SendResult sendResult;
            sendResult = producer.send(msg);

            if (SendStatus.SEND_OK == sendResult.getSendStatus()){
                log.info("发送MQ消息成功，message={}",message);
            }else {
                throw new MessageQueueException(sendResult.getSendStatus().toString());
            }

        }catch (Exception e){
        throw new MessageQueueException(e);
        }


    }
}
