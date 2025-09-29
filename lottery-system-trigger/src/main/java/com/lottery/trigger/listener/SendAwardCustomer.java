package com.lottery.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lottery.domain.award.event.SendAwardMessageEvent;
import com.lottery.domain.award.model.entity.DistributeAwardEntity;
import com.lottery.domain.award.service.IAwardService;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用户奖品记录消息消费者
 */
@Slf4j
@Component
public class SendAwardCustomer {

    @Value("${spring.rabbitmq.queue-name.send_award}")
    private String queueName;

    @Autowired
    private IAwardService awardService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.queue-name.send_award}"))
    public void listener(String message) {
        try {
            log.info("监听用户奖品发送消息 queueName: {} message: {}", queueName, message);
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
            });
            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();

            awardService.distributeAward(DistributeAwardEntity.builder()
                    .userId(sendAwardMessage.getUserId())
                    .orderId(sendAwardMessage.getOrderId())
                    .awardId(sendAwardMessage.getAwardId())
                    .awardConfig(sendAwardMessage.getAwardConfig())
                    .build());

        } catch (AppException ex) {
            log.error("出现业务异常,抛弃这条信息", ex);
        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 queueName: {} message: {}", queueName, message);
            throw e;
        }
    }

}
