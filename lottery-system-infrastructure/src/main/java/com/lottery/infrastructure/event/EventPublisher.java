package com.lottery.infrastructure.event;

import com.alibaba.fastjson.JSON;
import com.lottery.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送
 */
@Slf4j
@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String queueName, BaseEvent.EventMessage<?> eventMessage) {
        try {
            String messageJson = JSON.toJSONString(eventMessage);
            rabbitTemplate.convertAndSend(queueName, messageJson);
            log.info("发送MQ消息 queueName:{} message:{}", queueName, messageJson);
        } catch (Exception e) {
            log.error("发送MQ消息失败 queueName:{} message:{}", queueName, JSON.toJSONString(eventMessage), e);
            throw e;
        }
    }

    public void publish(String queueName, String messageJson) {
        try {
            rabbitTemplate.convertAndSend(queueName, messageJson);
            log.info("发送MQ消息 queueName:{} message:{}", queueName, messageJson);
        } catch (Exception e) {
            log.error("发送MQ消息失败 queueName:{} message:{}", queueName, messageJson, e);
            throw e;
        }
    }


}
