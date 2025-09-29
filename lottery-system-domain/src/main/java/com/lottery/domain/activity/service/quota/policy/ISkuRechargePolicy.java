package com.lottery.domain.activity.service.quota.policy;


import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

/**
 *  充值策略接口，包括；返利不用支付），积分订单（需要支付）
 */
public interface ISkuRechargePolicy {

    void skuRecharge(CreateQuotaOrderAggregate createQuotaOrderAggregate);

}
