package com.lottery.domain.credit.repository;

import com.lottery.domain.credit.model.aggregate.TradeAggregate;

import java.math.BigDecimal;

/**
 * 用户积分仓储
 */
public interface ICreditRepository {


    void doSavePayActivityOrder(TradeAggregate tradeAggregate);


    void doNoPayCreditOrder(TradeAggregate tradeAggregate);


    /**
     * 用户积分是否足够
     *
     * @param userId
     * @param outBusinessNo
     * @return
     */
    Boolean creditIsEnough(String userId, String outBusinessNo);


    /**
     * 获取用户剩余积分
     *
     * @param userId
     * @return
     */
    BigDecimal queryCreditAccountAvailableAmount(String userId);


    /**
     * 获取raffle_activity_order某个订单所需支付积分金额
     */
    BigDecimal queryActivityOrderPayAmount(String userId, String outBusinessNo);
}
