package com.lottery.domain.activity.event;

import com.lottery.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 活动sku库存清空信息
 */
@Component
public class ActivitySkuStockZeroMessageEvent extends BaseEvent<Long> {

    @Value("${spring.rabbitmq.queue-name.activity_sku_stock_zero}")
    private String queueName;


    @Override
    public EventMessage<Long> buildEventMessage(Long sku) {
        return EventMessage.<Long>builder()
                .messageId(RandomStringUtils.randomNumeric(11))
                .timestamp(LocalDateTime.now())
                .data(sku)
                .build();
    }

    @Override
    public String queueName() {
        return queueName;
    }
}
