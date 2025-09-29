package com.lottery.domain.credit.service;

import com.lottery.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.lottery.domain.credit.model.aggregate.TradeAggregate;
import com.lottery.domain.credit.model.entity.CreditAccountEntity;
import com.lottery.domain.credit.model.entity.CreditOrderEntity;
import com.lottery.domain.credit.model.entity.TaskEntity;
import com.lottery.domain.credit.model.entity.TradeEntity;
import com.lottery.domain.credit.model.valobj.TaskStateVO;
import com.lottery.domain.credit.repository.ICreditRepository;
import com.lottery.domain.credit.service.policy.ITradePolicy;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 积分调额服务【正逆向，增减积分】
 */
@Slf4j
@Service
public class CreditAdjustService implements ICreditAdjustService {

    @Resource
    private ICreditRepository creditRepository;

    @Resource
    private CreditAdjustSuccessMessageEvent creditAdjustSuccessMessageEvent;

    @Resource
    private Map<String, ITradePolicy> tradePolicyMap;

    @Override
    public String createCreditOrder(TradeEntity tradeEntity) {
        log.info("调整账户积分额度开始 userId:{} tradeName:{} amount:{}", tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getAmount());
        // 1. 创建账户积分实体
        CreditAccountEntity creditAccountEntity = CreditAccountEntity.builder()
                .userId(tradeEntity.getUserId())
                //注意 当这里是支付sku获取抽奖次数的时候 可能是null
                .adjustAmount(tradeEntity.getAmount())
                .build();
        // 2. 创建账户订单实体
        CreditOrderEntity creditOrderEntity = CreditOrderEntity.builder()
                .userId(tradeEntity.getUserId())
                .orderId(tradeEntity.getOrderId())
                .tradeName(tradeEntity.getTradeName())
                .tradeType(tradeEntity.getTradeType())
                //注意 当这里是支付sku获取抽奖次数的时候 可能是null
                .tradeAmount(tradeEntity.getAmount())
                .outBusinessNo(tradeEntity.getOutBusinessNo())
                .build();

        //3. 创建消息对象
        CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage.builder()
                .userId(tradeEntity.getUserId())
                .orderId(tradeEntity.getOrderId())
                .amount(tradeEntity.getAmount())
                .outBusinessNo(tradeEntity.getOutBusinessNo())
                .build();


        BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> eventMessage = creditAdjustSuccessMessageEvent.buildEventMessage(creditAdjustSuccessMessage);


        TaskEntity taskEntity = TaskEntity.builder()
                .queue(creditAdjustSuccessMessageEvent.queueName())
                .messageId(eventMessage.getMessageId())
                .message(eventMessage)
                .state(TaskStateVO.create)
                .build();

        // 4. 构建交易聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .creditAccountEntity(creditAccountEntity)
                .creditOrderEntity(creditOrderEntity)
                .taskEntity(taskEntity)
                .build();

        //5.用聚合对象将账户和订单写入数据库
        ITradePolicy tradePolicy = tradePolicyMap.get(tradeEntity.getOrderTradeType().getCode());
        if (tradePolicy == null) {
            throw new AppException(ResponseCode.TRADE_POLICY_IS_NULL.getCode(),ResponseCode.TRADE_POLICY_IS_NULL.getInfo());
        }
        tradePolicy.trade(tradeAggregate);
        log.info("调整账户积分额度完成 userId:{} orderId:{}", tradeEntity.getUserId(), creditOrderEntity.getOrderId());
        return creditOrderEntity.getOrderId();
    }


    /**
     * 获取用户剩余积分
     *
     * @param userId
     */
    @Override
    public BigDecimal queryCreditAccountAvailableAmount(String userId) {
        return creditRepository.queryCreditAccountAvailableAmount(userId);
    }
}
