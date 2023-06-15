package com.taki.cloud.mq.configuration;

import com.taki.cloud.mq.annotation.MessageQueueListener;
import com.taki.cloud.mq.consumer.KafkaConsumer;
import com.taki.cloud.mq.consumer.MessageListener;
import com.taki.cloud.mq.consumer.RocketMQConsumer;
import com.taki.cloud.mq.enums.MessageQueueTypeEnum;
import com.taki.cloud.mq.produer.KafkaMessageQueue;
import com.taki.cloud.mq.produer.MessageQueue;
import com.taki.cloud.mq.produer.RocketMessageQueue;
import com.taki.cloud.mq.properties.MessageQueueProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @ClassName MessageQueueAutoConfiguration
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 20:03
 * @Version 1.0
 */
// 针对我们的这个自动装配到底是否要激活
// 这块用的 就是@EnableConfigurationProperties，这个注解他会根据我们的业务系统里的yml配置文件
// 里面的一些指定的配置参数的值，来决定是否激活我们当前的这个自动装配
// 针对你的@Configuration类，激活指定的一些配置参数，激活和加载了以后，在bean装配的时候
// @ConditionalOnProperty注解，根据指定的参数的值，判断是否激活装配bean
@Configuration
@EnableConfigurationProperties(MessageQueueProperties.class)
public class MessageQueueAutoConfiguration {


    @Bean
    @ConditionalOnProperty(value = "taki.mq.kafka.enableProducer",havingValue = "ture")
    @ConditionalOnMissingBean(name = "kafkaProperties")

    public Properties kafkaProperties(MessageQueueProperties messageQueueProperties){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", messageQueueProperties.getKafka().getServer());
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // kafka producer都很重要
        // acks，acks=1，kafka来说，你要写入的是一个topic，划分为很多的partitions
        // 对于每一个partition分区来说，他都是有多个副本的，leader+followers
        // 读写针对leader partition去走的，followers跟会leader进行数据之间的同步
        // insync list，保存的是partition副本集合
        // acks=all，你写入数据的时候，必须确保insync列表里的所有的partition副本都要写入成功，才可以算本次写入success了
        // 好处就是说，你的数据一般来说不会丢失，写入成功了，多个副本都有数据，某台kafka机器宕机，单个副本了
        // 缺点是，会影响写入性能和吞吐量
        // acks=1，每次写入，只要能够进入leader partition里面去就可以了，是否写入其他副本，好处是速度快
        // 缺点，万一刚写完leader，leader直接宕机崩溃了，导致follower没有同步，数据可能会丢
        properties.put("acks", "1");
        // producer发送出去一条消息后，还没收到这条消息的response响应，此时这条消息in flight，半空中
        // 此时是否可以继续往外发送消息呢？设置为1，概念，不会乱序，吞吐量么有那么高
        properties.put("max.in.flight.requests.per.connection", "1");
        // 写失败重试次数
        properties.put("retries", Integer.MAX_VALUE);
        // 写出去的一条消息，会先在producer buffer缓冲一下，如果说攒够了16kb的数据，赞成一个batch
        // 但是如果说超过10ms了，还没攒出来16kb的batch，此时必须发送消息出去了
        properties.put("batch.size", 16 * 1024);
        properties.put("linger.ms", 10);
        properties.put("buffer.memory", 32 * 1024 * 1024);
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "taki.mq.kafka.enableProducer", havingValue = "true")
    public KafkaProducer<String, String> kafkaProducer(@Qualifier("kafkaProperties") Properties properties) {
        return new KafkaProducer<>(properties);
    }

    @Bean(name = "kafkaMessageQueue")
    @ConditionalOnClass(KafkaProducer.class)
    @ConditionalOnProperty(value = "taki.mq.kafka.enableProducer", havingValue = "true")
    public MessageQueue kafkaMessageQueue(KafkaProducer<String, String> kafkaProducer) {
        return new KafkaMessageQueue(kafkaProducer);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "taki.mq.rocketmq.enableProducer", havingValue = "true")
    public DefaultMQProducer defaultMQProducer(MessageQueueProperties messageQueueProperties) throws MQClientException {
        // rocketmq也是一样的，提取一些参数，基于Rocketmq的API，封装producer
        TransactionMQProducer transactionMQProducer =
                new TransactionMQProducer(messageQueueProperties.getRocketMQ().getProducerGroup());
        transactionMQProducer.setNamesrvAddr(messageQueueProperties.getRocketMQ().getServer());
        transactionMQProducer.start();
        return transactionMQProducer;
    }

    @Bean(name = "rocketMqMessageQueue")
    @ConditionalOnClass(DefaultMQProducer.class)
    @ConditionalOnProperty(value = "taki.mq.rocketmq.enableProducer", havingValue = "true")
    public MessageQueue rocketMessageQueue(DefaultMQProducer defaultMQProducer) {
        return new RocketMessageQueue(defaultMQProducer);
    }

    @Bean
    @ConditionalOnProperty(value = "taki.mq.kafka.enableConsumer", havingValue = "true")
    public KafkaConsumer kafkaConsumer(MessageQueueProperties messageQueueProperties, List<MessageListener> listeners) {
        List<MessageListener> listenersResult =
                listeners.stream().filter(this::matchKafka).collect(Collectors.toList());
        return new KafkaConsumer( listenersResult,messageQueueProperties);
    }


    @Bean
    @ConditionalOnProperty(value = "taki.mq.rocketmq.enableConsumer", havingValue = "true")
    public RocketMQConsumer rocketMqConsumer(MessageQueueProperties messageQueueProperties,
                                             List<MessageListener> listeners) throws Exception {
        List<MessageListener> listenersResult =
                listeners.stream().filter(this::matchRocketMQ).collect(Collectors.toList());
        return new RocketMQConsumer(listenersResult,messageQueueProperties);
    }

    private boolean matchRocketMQ(MessageListener messageListener) {
        Class<? extends MessageListener> clazz = messageListener.getClass();
        if (!clazz.isAnnotationPresent(MessageQueueListener.class)) {
            return false;
        }
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
        return annotation.type().equals(MessageQueueTypeEnum.ROCKETMQ);
    }

    private boolean matchKafka(MessageListener messageListener) {
        Class<? extends MessageListener> clazz = messageListener.getClass();
        if (!clazz.isAnnotationPresent(MessageQueueListener.class)) {
            return false;
        }
        MessageQueueListener annotation = clazz.getAnnotation(MessageQueueListener.class);
        return annotation.type().equals(MessageQueueTypeEnum.KAFKA);
    }

}
