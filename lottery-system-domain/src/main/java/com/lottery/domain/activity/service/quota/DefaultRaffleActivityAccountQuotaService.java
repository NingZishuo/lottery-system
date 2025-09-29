package com.lottery.domain.activity.service.quota;

import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.IRaffleActivitySkuStockService;
import com.lottery.domain.activity.service.quota.policy.ISkuRechargePolicy;
import com.lottery.domain.activity.service.quota.rule.chain.factory.DefaultActivityActionChainFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class DefaultRaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuotaService implements IRaffleActivitySkuStockService {


    @Autowired
    public DefaultRaffleActivityAccountQuotaService(IActivityRepository activityRepository, DefaultActivityActionChainFactory actionChainFactory, Map<String, ISkuRechargePolicy> skuRechargePolicyMap) {
        super(activityRepository, actionChainFactory, skuRechargePolicyMap);
    }


    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity,RaffleActivitySkuEntity activitySkuEntity ,RaffleActivityEntity activityEntity, RaffleActivityCountEntity activityCountEntity) {

        LocalDateTime now = LocalDateTime.now();
        String formatMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String formatDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String userId = skuRechargeEntity.getUserId();
        Long activityId = activityEntity.getActivityId();

        RaffleActivityAccountEntity raffleActivityAccountEntity = RaffleActivityAccountEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(activityCountEntity.getTotalCount())
                    .totalCountSurplus(activityCountEntity.getTotalCount())
                    .build();

        RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = RaffleActivityAccountMonthEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .month(formatMonth)
                .monthCount(activityCountEntity.getTotalCount())
                .monthCountSurplus(activityCountEntity.getTotalCount())
                .build();

        RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = RaffleActivityAccountDayEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .day(formatDay)
                .dayCount(activityCountEntity.getTotalCount())
                .dayCountSurplus(activityCountEntity.getTotalCount())
                .build();


        RaffleActivityOrderEntity raffleActivityOrderEntity = RaffleActivityOrderEntity.builder()
                .userId(skuRechargeEntity.getUserId())
                .sku(skuRechargeEntity.getSku())
                .activityId(activityEntity.getActivityId())
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                //订单ID - 该sku来自哪个返利订单
                .orderId(skuRechargeEntity.getOrderId())
                .orderTime(now)
                .totalCount(activityCountEntity.getTotalCount())
                .payAmount(activitySkuEntity.getProductPrice())
                .outBusinessNo(skuRechargeEntity.getOutBusinessNo())
                .build();

        return CreateQuotaOrderAggregate.builder()
                .raffleActivityAccountEntity(raffleActivityAccountEntity)
                .raffleActivityAccountMonthEntity(raffleActivityAccountMonthEntity)
                .raffleActivityAccountDayEntity(raffleActivityAccountDayEntity)
                .raffleActivityOrderEntity(raffleActivityOrderEntity)
                .build();

    }

    /**
     * 更新ActivityOrder对应支付订单为completed
     * @param deliveryOrderEntity
     */
    @Override
    public void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity) {
        activityRepository.updateActivityOrder(deliveryOrderEntity);
    }

    /**
     * 获取sku库存消耗队列
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public ActivitySkuStockKeyVO takeQueueValue(Long sku) throws InterruptedException {
        return activityRepository.takeQueueValue(sku);
    }

    /**
     * 获取skuTotal库存消耗队列
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public Long takeTotalQueueValue() throws InterruptedException {
        return activityRepository.takeTotalQueueValue();
    }

    /**
     * 清空队列
     */
    @Override
    public void clearQueueValue(Long sku) {
        activityRepository.clearQueueValue(sku);
    }

    /**
     * 延迟队列 + 任务趋势更新活动sku库存
     *
     * @param sku
     */
    @Override
    public void updateActivitySkuStock(Long sku) {
        activityRepository.updateActivitySkuStock(sku);
    }

    /**
     * 缓存库存消耗完毕,清空数据库库存
     *
     * @param sku
     */
    @Override
    public void clearActivitySkuStock(Long sku) {
        activityRepository.clearActivitySkuStock(sku);
    }


    /**
     * 查询用户在某活动的账户额度
     *
     * @param userId
     * @param activityId
     */
    @Override
    public RaffleActivityAccountEntity queryActivityAccount(String userId, Long activityId) {
        return activityRepository.queryRaffleActivityAccount(userId, activityId);
    }



}
