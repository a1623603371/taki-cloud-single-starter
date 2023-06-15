package com.taki.cloud.mq.consumer;

import com.taki.cloud.mq.annotation.MessageQueueListener;
import com.taki.cloud.mq.properties.MessageQueueProperties;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import sun.plugin2.message.Message;

import java.util.List;

/**
 * @ClassName RocketMQConsumer
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 18:50
 * @Version 1.0
 */
public class RocketMQConsumer {

    private final List<MessageListener> messageListeners;

    private final MessageQueueProperties messageQueueProperties;

    public RocketMQConsumer(List<MessageListener> messageListeners, MessageQueueProperties messageQueueProperties) throws MQClientException {
        this.messageListeners = messageListeners;
        this.messageQueueProperties = messageQueueProperties;
        initConsumer();
    }

    private void initConsumer() throws MQClientException {

        // 对于我们 rocketmq consumer，本身就是独立线程回去进行消费
        //针对每个 listener 创建一个独立rocketmq原生consumer每个原生consumer消费指定的topic的消息
        // 消费一个消息，就会给我们的listener监听器
        for (MessageListener messageListener : messageListeners) {

            Class<? extends MessageListener> clazz = messageListener.getClass();

            MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(annotation.consumerGroup());
            consumer.setNamesrvAddr(messageQueueProperties.getRocketMQ().getServer());

            //tgs,在发送消息的topic里去的时候，每个消息可以带上tags在broker端进行tags存储
            //消费的时候可以指定针对tags进行消费，broker端，会筛选指定的tags对应消息
            //拿到tags对应消息

                consumer.subscribe(annotation.topic(),annotation.tags());
                consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context)->{

                    for (MessageExt msg : msgs) {


                        String message = new String(msg.getBody());
                        // 进行消息回调，是我们自定义的listener回调
                        messageListener.onMessage(message);

                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                });

            consumer.start();
        }
    }
}
