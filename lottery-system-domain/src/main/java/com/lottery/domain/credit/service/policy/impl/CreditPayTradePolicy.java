package com.lottery.domain.credit.service.policy.impl;


import com.lottery.domain.credit.model.aggregate.TradeAggregate;
import com.lottery.domain.credit.model.entity.CreditAccountEntity;
import com.lottery.domain.credit.model.entity.CreditOrderEntity;
import com.lottery.domain.credit.repository.ICreditRepository;
import com.lottery.domain.credit.service.policy.ITradePolicy;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 积分兑换，扣减积分 同时发送mq 让机会必定发放
 */
@Service("credit_pay_trade_order_complete")
public class CreditPayTradePolicy implements ITradePolicy {

    @Autowired
    private ICreditRepository creditRepository;

    @Override
    public void trade(TradeAggregate tradeAggregate) {

        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();

        //1.查询订单所需支付积分
        BigDecimal payAmount = creditRepository.queryActivityOrderPayAmount(creditOrderEntity.getUserId(), creditOrderEntity.getOutBusinessNo());
        if (payAmount == null) {
            throw new AppException(ResponseCode.ACTIVITY_ORDER_IS_ENOUGH.getCode(),ResponseCode.ACTIVITY_ORDER_IS_ENOUGH.getInfo());
        }
        creditAccountEntity.setAdjustAmount(payAmount.negate());
        creditOrderEntity.setTradeAmount(payAmount.negate());

        //2.判断积分是否足够支付订单
        Boolean result  = creditRepository.creditIsEnough(creditOrderEntity.getUserId(),creditOrderEntity.getOutBusinessNo());
        if (!result) {
             throw new AppException(ResponseCode.CREDIT_IS_NOT_ENOUGH.getCode(),ResponseCode.CREDIT_IS_NOT_ENOUGH.getInfo());
        }


        creditRepository.doSavePayActivityOrder(tradeAggregate);
    }
}
