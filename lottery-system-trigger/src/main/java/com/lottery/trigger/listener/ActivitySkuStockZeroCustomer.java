package com.lottery.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.lottery.domain.activity.service.IRaffleActivitySkuStockService;
import com.lottery.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 活动sku库存耗尽
 */
@Slf4j
@Component
public class ActivitySkuStockZeroCustomer {

    @Value("${spring.rabbitmq.queue-name.activity_sku_stock_zero}")
    private String queueName;

    @Resource
    private IRaffleActivitySkuStockService activitySkuStock;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.queue-name.activity_sku_stock_zero}"))
    public void listener(String message) {
        try {
            log.info("监听活动sku库存消耗为0消息 queueName: {} message: {}", queueName, message);
            // 转换对象
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<Long>>() {
            });
            Long sku = eventMessage.getData();
            // 清空队列 「此时就不需要延迟更新数据库记录了」
            activitySkuStock.clearQueueValue(sku);
            // 更新库存为0
            activitySkuStock.clearActivitySkuStock(sku);
        } catch (Exception e) {
            log.error("监听活动sku库存消耗为0消息，消费失败 queueName: {} message: {}", queueName, message);
            throw e;
        }
    }

}
