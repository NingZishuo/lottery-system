package com.lottery.domain.activity.service.quota;

import com.lottery.domain.activity.model.entity.RaffleActivityCountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityOrderEntity;
import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.quota.policy.ISkuRechargePolicy;
import com.lottery.domain.activity.service.quota.rule.chain.factory.DefaultActivityActionChainFactory;

import java.util.Map;

/**
 * 抽奖活动支撑类
 */
public abstract class RaffleActivityAccountQuotaSupport {

    protected IActivityRepository activityRepository;

    protected DefaultActivityActionChainFactory actionChainFactory;

    protected Map<String, ISkuRechargePolicy> skuRechargePolicyMap;

    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityActionChainFactory actionChainFactory,Map<String, ISkuRechargePolicy> skuRechargePolicyMap) {
        this.activityRepository = activityRepository;
        this.actionChainFactory = actionChainFactory;
        this.skuRechargePolicyMap = skuRechargePolicyMap;
    }

    public RaffleActivitySkuEntity queryRaffleActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public RaffleActivityEntity queryRaffleActivity(Long activityId) {
        return activityRepository.queryRaffleActivity(activityId);
    }

    public RaffleActivityCountEntity queryRaffleActivityCount(Long activityCountId) {
        return activityRepository.queryRaffleActivityCount(activityCountId);
    }

    public RaffleActivityOrderEntity queryRaffleActivityOrder(String userId,String outBusinessNo) {
        return activityRepository.queryRaffleActivityOrder(userId,outBusinessNo);
    }
}
