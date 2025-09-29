package com.lottery.domain.credit.service;

import com.lottery.domain.credit.model.entity.TradeEntity;

import java.math.BigDecimal;

/**
 * 积分调额接口【正逆向，增减积分】
 */
public interface ICreditAdjustService {

    /**
     * 创建增加积分额度订单
     */
    String createCreditOrder(TradeEntity tradeEntity);

    /**
     * 获取用户剩余积分
     */
    BigDecimal queryCreditAccountAvailableAmount(String userId);

}
