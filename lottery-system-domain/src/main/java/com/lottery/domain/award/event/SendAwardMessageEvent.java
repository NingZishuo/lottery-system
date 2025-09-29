package com.lottery.domain.award.event;

import com.lottery.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *  用户奖品记录事件消息
 */
@Component
public class SendAwardMessageEvent extends BaseEvent<SendAwardMessageEvent.SendAwardMessage> {

    @Value("${spring.rabbitmq.queue-name.send_award}")
    private String queueName;

    @Override
    public EventMessage<SendAwardMessage> buildEventMessage(SendAwardMessage data) {
        return EventMessage.<SendAwardMessage>builder()
                .messageId(RandomStringUtils.randomNumeric(11))
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    @Override
    public String queueName() {
        return queueName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendAwardMessage {

        /**
         * 用户ID
         */
        private String userId;
        /**
         * 订单ID
         */
        private String orderId;
        /**
         * 奖品ID
         */
        private Long awardId;

        /**
         * 奖品标题（名称）
         */
        private String awardTitle;

        /**
         * 奖品配置
         */
        private String awardConfig;


    }

}
