package com.lottery.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sku订单交易类型
 */
@Getter
@AllArgsConstructor
public enum SkuRechargeTypeVO {

    credit_pay_trade_order_create("credit_pay_trade_order_create","积分兑换，需要支付类交易,积分换次数获取订单创建"),
    rebate_no_pay_trade_chance_direct_distribute("rebate_no_pay_trade_chance_direct_distribute", "返利奖品，直接发放抽奖次数"),
    ;

    private final String code;
    private final String desc;

}
