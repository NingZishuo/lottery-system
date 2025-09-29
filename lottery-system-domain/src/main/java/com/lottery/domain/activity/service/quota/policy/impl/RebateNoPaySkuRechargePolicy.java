package com.lottery.domain.activity.service.quota.policy.impl;

import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.valobj.ActivityQuotaOrderStateVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.quota.policy.ISkuRechargePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * 返利无支付交易订单，直接充值次数到账
 */
@Service("rebate_no_pay_trade_chance_direct_distribute")
public class RebateNoPaySkuRechargePolicy implements ISkuRechargePolicy {

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public void skuRecharge(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.getRaffleActivityOrderEntity().setState(ActivityQuotaOrderStateVO.completed);
        //注意这里不设置的话 数据库显示为null 默认值
        createQuotaOrderAggregate.getRaffleActivityOrderEntity().setPayAmount(BigDecimal.ZERO);
        activityRepository.doSaveNoPayActivityOrder(createQuotaOrderAggregate);
    }
}
