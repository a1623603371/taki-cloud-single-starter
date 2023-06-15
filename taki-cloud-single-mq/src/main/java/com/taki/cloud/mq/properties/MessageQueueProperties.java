package com.taki.cloud.mq.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName MessageQueueProperties
 * @Description TODO
 * @Author Long
 * @Date 2023/6/15 16:13
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "taki.mq")
public class MessageQueueProperties {


    private Kafka kafka;

    private RocketMQ rocketMQ;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
  public static class Kafka{

        private Boolean enableProducer;

        private Boolean enableConsumer;

        private String server;

  }


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public  static  class RocketMQ{

        private Boolean enableProducer;

        private Boolean enableConsumer;

        private String server;

        private String producerGroup = "taki-cloud-group";

  }
}
