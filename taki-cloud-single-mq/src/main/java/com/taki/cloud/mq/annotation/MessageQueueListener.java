package com.taki.cloud.mq.annotation;

import com.taki.cloud.mq.enums.MessageQueueTypeEnum;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageQueueListener {

    String topic();


    String consumerGroup() default "default-consumer-group";



    MessageQueueTypeEnum type () default MessageQueueTypeEnum.UNKNOWN;



    String tags() default "*";
}
