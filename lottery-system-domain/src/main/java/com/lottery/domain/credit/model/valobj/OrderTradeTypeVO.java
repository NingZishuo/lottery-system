package com.lottery.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单交易类型
 */
@Getter
@AllArgsConstructor
public enum OrderTradeTypeVO {

    credit_pay_trade_order_complete("credit_pay_trade_order_complete","积分兑换，需要支付类交易,扣减积分,发送mq获取抽奖次数"),
    rebate_no_pay_trade_credit_direct_distribute("rebate_no_pay_trade_credit_direct_distribute", "返利奖品，直接发放积分"),
    ;

    private final String code;
    private final String desc;

}
