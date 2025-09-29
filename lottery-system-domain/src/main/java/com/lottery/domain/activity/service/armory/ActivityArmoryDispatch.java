package com.lottery.domain.activity.service.armory;

import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;
import com.lottery.domain.activity.repository.IActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动装配实现类
 */
@Service
@Slf4j
public class ActivityArmoryDispatch implements IActivityArmory,IActivityDispatch {

    @Autowired
    private IActivityRepository activityRepository;

    /**
     * 活动装配sku
     *
     * @param sku
     * @return
     */
    @Override
    public boolean assembleActivitySku(Long sku) {
        RaffleActivitySkuEntity raffleActivitySkuEntity = activityRepository.queryActivitySku(sku);

        this.cacheActivitySkuStockCount(raffleActivitySkuEntity.getActivityId(), raffleActivitySkuEntity.getSku(), raffleActivitySkuEntity.getStockCount());

        activityRepository.queryRaffleActivity(raffleActivitySkuEntity.getActivityId());

        activityRepository.queryRaffleActivityCount(raffleActivitySkuEntity.getActivityCountId());

        return true;
    }

    /**
     * 装配活动及其对应Sku
     * @param activityId
     * @return
     */
    @Override
    public boolean assembleActivityAndSkuByActivityId(Long activityId) {
        //0.装配活动
        activityRepository.queryRaffleActivity(activityId);

        List<RaffleActivitySkuEntity> activitySkuEntityList = activityRepository.queryActivitySkuList(activityId);
        //1.轮流装配  Sku 库存 Sku给的抽奖次数
        for (RaffleActivitySkuEntity activitySkuEntity : activitySkuEntityList) {
            activityRepository.queryActivitySku(activitySkuEntity.getSku());
            cacheActivitySkuStockCount(activitySkuEntity.getActivityId(), activitySkuEntity.getSku(), activitySkuEntity.getStockCount());
            activityRepository.queryRaffleActivityCount(activitySkuEntity.getActivityCountId());

        }

        return true;
    }

    private void cacheActivitySkuStockCount(Long activityId, Long sku, Integer stockCount) {
        //这里还是用了总库存啊 没用剩余库存
        activityRepository.cacheActivitySkuStockCount(activityId, sku, stockCount);
    }

    /**
     * 根据策略ID和奖品ID，扣减奖品缓存库存
     *
     * @param activityId  活动Id
     * @param sku         互动SKU
     * @param endDateTime 活动结束时间，根据结束时间设置加锁的key为结束时间
     * @return 扣减结果
     */
    @Override
    public boolean subtractionActivitySkuStock(Long activityId, Long sku, LocalDateTime endDateTime) {
        return activityRepository.subtractionActivitySkuStock(activityId,sku,endDateTime);
    }
}
