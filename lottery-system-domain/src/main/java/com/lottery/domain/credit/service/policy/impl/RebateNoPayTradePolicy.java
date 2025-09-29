package com.lottery.domain.credit.service.policy.impl;

import com.lottery.domain.credit.model.aggregate.TradeAggregate;
import com.lottery.domain.credit.repository.ICreditRepository;
import com.lottery.domain.credit.service.policy.ITradePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 返利无支付交易订单，直接充值到账
 */
@Service("rebate_no_pay_trade_credit_direct_distribute")
public class RebateNoPayTradePolicy implements ITradePolicy {

    @Autowired
    private ICreditRepository creditRepository;

    @Override
    public void trade(TradeAggregate tradeAggregate) {
        tradeAggregate.setTaskEntity(null);
        creditRepository.doNoPayCreditOrder(tradeAggregate);
    }
}
