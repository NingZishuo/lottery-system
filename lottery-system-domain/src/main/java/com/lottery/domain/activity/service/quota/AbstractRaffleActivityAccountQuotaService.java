package com.lottery.domain.activity.service.quota;

import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.lottery.domain.activity.service.quota.policy.ISkuRechargePolicy;
import com.lottery.domain.activity.service.quota.rule.chain.IActivityActionChain;
import com.lottery.domain.activity.service.quota.rule.chain.factory.DefaultActivityActionChainFactory;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 *  抽奖活动抽象类，定义标准的流程
 */
@Slf4j
public abstract class AbstractRaffleActivityAccountQuotaService extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {

    public AbstractRaffleActivityAccountQuotaService(IActivityRepository activityRepository, DefaultActivityActionChainFactory actionChainFactory, Map<String, ISkuRechargePolicy> skuRechargePolicyMap) {
        super(activityRepository, actionChainFactory,skuRechargePolicyMap);
    }

    /**
     * 创建Sku账户充值订单 , 给用户增加抽奖次数
     *
     * @param skuRechargeEntity
     * @return
     */
    @Override
    public RaffleActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        //1.校验基础信息
        Long sku = skuRechargeEntity.getSku();
        String userId = skuRechargeEntity.getUserId();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (sku == null || userId.isEmpty() || outBusinessNo.isEmpty()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        RaffleActivityOrderEntity raffleActivityOrderEntity =  this.queryRaffleActivityOrder(userId,outBusinessNo);

        if (raffleActivityOrderEntity!=null) {
            throw new AppException(ResponseCode.REPEAT_DISTRIBUTE_SKU.getCode(),ResponseCode.REPEAT_DISTRIBUTE_SKU.getInfo());
        }

        //2.查询基础信息
        // 2.1 通过sku查询活动信息
        RaffleActivitySkuEntity activitySkuEntity = this.queryRaffleActivitySku(sku);
        if (activitySkuEntity == null) {
            throw new AppException(ResponseCode.SKU_IS_NULL.getCode(),ResponseCode.SKU_IS_NULL.getInfo());
        }
        // 2.2 查询活动信息
        RaffleActivityEntity activityEntity = this.queryRaffleActivity(activitySkuEntity.getActivityId());
        if (activityEntity == null) {
            throw new AppException(ResponseCode.ACTIVITY_IS_NULL.getCode(),ResponseCode.ACTIVITY_IS_NULL.getInfo());
        }
        // 2.3 查询次数信息（用户在活动上可参与的次数）
        RaffleActivityCountEntity activityCountEntity = this.queryRaffleActivityCount(activitySkuEntity.getActivityCountId());
        if (activityCountEntity == null) {
            throw new AppException(ResponseCode.ACTIVITY_COUNT_IS_NULL.getCode(),ResponseCode.ACTIVITY_COUNT_IS_NULL.getInfo());
        }

        //3.使用活动链过滤基础信息 如是否在活动时间中 某活动库存够吗?
        IActivityActionChain activityActionChain = actionChainFactory.openActivityActionChain();

        activityActionChain.action(activitySkuEntity, activityEntity, activityCountEntity);

        //4.构建订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = this.buildOrderAggregate(skuRechargeEntity,activitySkuEntity,activityEntity,activityCountEntity);

        //5.用聚合对象将账户和订单写入数据库
        ISkuRechargePolicy skuRechargePolicy = skuRechargePolicyMap.get(skuRechargeEntity.getSkuRechargeTypeVO().getCode());
        if (skuRechargePolicy == null) {
            throw new AppException(ResponseCode.TRADE_POLICY_IS_NULL.getCode(),ResponseCode.TRADE_POLICY_IS_NULL.getInfo());
        }
        skuRechargePolicy.skuRecharge(createQuotaOrderAggregate);

        return createQuotaOrderAggregate.getRaffleActivityOrderEntity();
    }


    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity,RaffleActivitySkuEntity activitySkuEntity ,RaffleActivityEntity activityEntity, RaffleActivityCountEntity activityCountEntity);
}
