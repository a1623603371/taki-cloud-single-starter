package com.taki.cloud.mq.produer;

import com.taki.cloud.mq.exception.MessageQueueException;
import com.taki.cloud.mq.properties.MessageQueueProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.util.StringUtils;

/**
 * @ClassName KafkaMessageQueue
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 19:28
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class KafkaMessageQueue  implements  MessageQueue{

    // kafka properties 构键出来
    private KafkaProducer<String,String> kafkaProducer;

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

        if (!StringUtils.isEmpty(tags)){
            log.warn("Kafka not  support  parameter :tags -》{}",tags);
        }

        if (delayTimeLevel > 0){
            log.warn("Kafka not  support delay feature delayTimeLevel -》{}",tags);
        }

        ProducerRecord<String,String> record;

        if(!StringUtils.isEmpty(key)) {
            //kafka ,默认来说，消息会均匀分散各个partition 里，数据分片存储
            // 同一个topic 里的数量，会分散存储kafka 多台机器
            //均匀分散了以后，数据都是乱序，可能需要让某个业务id对应多个多条消息必须进入一个partition 里去
            // 加入 一个key ，key是业务id，确保同一个业务id的数据进入到同一个partition里，有序的

        record = new ProducerRecord<>(topic,key,message);


        }else {

            record = new ProducerRecord<>(topic,message);
        }
        //还需要 接收一个 exception 的回调，发送结果 的需要 进行回调 通知这个
        kafkaProducer.send(record,(metadata,exception)->{
            if (exception == null){
                log.info("发送消息到kafka 成功");
            }else {

                log.error("发送消息到kafka失败：{}",exception);

                throw new RuntimeException(exception);
            }
        });


    }
}
