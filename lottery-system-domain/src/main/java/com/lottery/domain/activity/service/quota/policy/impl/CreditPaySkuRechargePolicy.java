package com.lottery.domain.activity.service.quota.policy.impl;

import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.valobj.ActivityQuotaOrderStateVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.quota.policy.ISkuRechargePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 积分兑换，支付类订单 先创建积分换次数订单
 */
@Service("credit_pay_trade_order_create")
public class CreditPaySkuRechargePolicy implements ISkuRechargePolicy {

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public void skuRecharge(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.getRaffleActivityOrderEntity().setState(ActivityQuotaOrderStateVO.wait_pay);
        activityRepository.doSavePayActivityOrder(createQuotaOrderAggregate);
    }
}
