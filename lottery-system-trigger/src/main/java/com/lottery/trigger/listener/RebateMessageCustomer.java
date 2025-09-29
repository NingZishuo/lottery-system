package com.lottery.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.lottery.domain.activity.model.entity.SkuRechargeEntity;
import com.lottery.domain.activity.model.valobj.SkuRechargeTypeVO;
import com.lottery.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.lottery.domain.credit.model.entity.TradeEntity;
import com.lottery.domain.credit.model.valobj.OrderTradeTypeVO;
import com.lottery.domain.credit.model.valobj.TradeNameVO;
import com.lottery.domain.credit.model.valobj.TradeTypeVO;
import com.lottery.domain.credit.service.ICreditAdjustService;
import com.lottery.domain.rebate.event.SendRebateMessageEvent;
import com.lottery.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 监听；行为返利消息
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    @Value("${spring.rabbitmq.queue-name.send_rebate}")
    private String queueName;

    @Autowired
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Autowired
    private ICreditAdjustService creditAdjustService;


    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.queue-name.send_rebate}"))
    public void listener(String message) {

        // 1. 转换消息
        BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
        });
        String messageId = eventMessage.getMessageId();
        SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
        DailyBehaviorRebateVO dailyBehaviorRebateVO = rebateMessage.getDailyBehaviorRebateVO();
        try {
            log.info("监听用户行为返利消息 messageId:{}, queueName: {} message: {}", messageId, queueName, message);
            switch (dailyBehaviorRebateVO.getRebateTypeVO()) {
                //2.SKU 入账用户奖励次数
                case SKU:
                    raffleActivityAccountQuotaService.createSkuRechargeOrder(SkuRechargeEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .orderId(rebateMessage.getOrderId())
                            .sku(Long.valueOf(rebateMessage.getDailyBehaviorRebateVO().getRebateConfig()))
                            .outBusinessNo(rebateMessage.getOutBusinessNo())
                            .skuRechargeTypeVO(SkuRechargeTypeVO.rebate_no_pay_trade_chance_direct_distribute)
                            .build());
                    break;
                //3.INTEGRAl 直接给用户增加积分
                case INTEGRAL:
                    creditAdjustService.createCreditOrder(TradeEntity.builder()
                            .userId(rebateMessage.getUserId())
                            .orderId(rebateMessage.getOrderId())
                            .tradeName(TradeNameVO.REBATE)
                            .tradeType(TradeTypeVO.FORWARD)
                            .amount(new BigDecimal(dailyBehaviorRebateVO.getRebateConfig()))
                            .outBusinessNo(rebateMessage.getOutBusinessNo())
                            .orderTradeType(OrderTradeTypeVO.rebate_no_pay_trade_credit_direct_distribute)
                            .build());
                    break;
            }
        } catch (AppException ex) {
            log.error("出现业务异常,抛弃这条信息", ex);
        } catch (Exception e) {
            log.error("监听用户行为返利消息失败 messageId:{}, queueName: {} message: {}", messageId, queueName, message, e);
            throw e;
        }

    }

}
