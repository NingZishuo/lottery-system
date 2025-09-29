package com.lottery.domain.rebate.event;

import com.lottery.domain.rebate.model.valobj.DailyBehaviorRebateVO;
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
 * 发送返利消息事件
 */
@Component
public class SendRebateMessageEvent extends BaseEvent<SendRebateMessageEvent.RebateMessage> {

    @Value("${spring.rabbitmq.queue-name.send_rebate}")
    private String queueName;

    @Override
    public EventMessage<RebateMessage> buildEventMessage(RebateMessage data) {
        return EventMessage.<RebateMessage>builder()
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
    public static class RebateMessage {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 订单ID
         */
        private String orderId;
        /**
         * 返利  其实只有rebateConfig 这个属性有用
         * 其它属性仅作展示
         */
        DailyBehaviorRebateVO dailyBehaviorRebateVO;
        /**
         * 防重ID
         */
        private String outBusinessNo;
    }

}
