package com.lottery.domain.activity.service;

import com.lottery.domain.activity.model.entity.DeliveryOrderEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityAccountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityOrderEntity;
import com.lottery.domain.activity.model.entity.SkuRechargeEntity;

/**
 * 抽奖活动订单接口
 */
public interface IRaffleActivityAccountQuotaService {

    /**
     * 创建Sku账户充值订单 , 给用户增加抽奖次数
     * sku可能是返利 可能是积分支付来的
     * @param skuRechargeEntity
     */
    RaffleActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);

    /**
     * 积分支付成功回调函数
     * @param deliveryOrderEntity
     */
    void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity);

    /**
     * 查询用户在某活动的账户额度
     */
    RaffleActivityAccountEntity queryActivityAccount(String userId, Long activityId);

}
