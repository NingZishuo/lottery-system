package com.lottery.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.lottery.domain.activity.model.entity.DeliveryOrderEntity;
import com.lottery.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.lottery.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *  积分调整成功消息
 */
@Slf4j
@Component
public class CreditAdjustSuccessCustomer {

    @Value("${spring.rabbitmq.queue-name.credit_adjust_success}")
    private String topic;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.queue-name.credit_adjust_success}"))
    public void listener(String message) {
        try {
            log.info("监听积分账户调整成功消息，进行交易商品发货 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage>>() {
            });
            CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = eventMessage.getData();
            // 积分发货
            DeliveryOrderEntity deliveryOrderEntity = DeliveryOrderEntity.builder()
                    .userId(creditAdjustSuccessMessage.getUserId())
                    .outBusinessNo(creditAdjustSuccessMessage.getOutBusinessNo())
                    .build();
            raffleActivityAccountQuotaService.updateActivityOrder(deliveryOrderEntity);

        } catch (AppException ex) {
            log.error("出现业务异常,抛弃这条信息", ex);
        } catch (Exception e) {
            log.error("监听积分账户调整成功消息，进行交易商品发货失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
    }

}
