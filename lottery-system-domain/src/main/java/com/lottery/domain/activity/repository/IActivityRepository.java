package com.lottery.domain.activity.repository;

import com.lottery.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  活动仓储接口
 */
public interface IActivityRepository {

    /**
     * 查询活动sku实体对象
     * @param sku  标识符
     * @return
     */
    RaffleActivitySkuEntity queryActivitySku(Long sku);

    /**
     * 查询活动实体对象
     * @param activityId
     * @return
     */
    RaffleActivityEntity queryRaffleActivity(Long activityId);

    /**
     * 查询活动次数实体对象
     * @param activityCountId
     * @return
     */
    RaffleActivityCountEntity queryRaffleActivityCount(Long activityCountId);


    /**
     * sku充值账户 - 非购买
     * @param createQuotaOrderAggregate
     */
    void doSaveNoPayActivityOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
     * sku充值账户 - 购买
     * @param createQuotaOrderAggregate
     */
    void doSavePayActivityOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    /**
     * 缓存sku的库存
     * @param activityId
     * @param sku
     * @param stockCount
     */
    void cacheActivitySkuStockCount(Long activityId, Long sku, Integer stockCount);

    /**
     * redis扣减库存
     * @param activityId
     * @param sku
     * @param endDateTime
     * @return
     */
    boolean subtractionActivitySkuStock(Long activityId, Long sku, LocalDateTime endDateTime);

    /**
     * 发送消息队列扣减库存
     * @param activitySkuStockKeyVO
     */
    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    /**
     * 获取redis消息队列
     * @return
     */
    ActivitySkuStockKeyVO takeQueueValue(Long sku);


    /**
     * 获取总的redis消息队列
     * @return
     */
    Long takeTotalQueueValue();



    /**
     * 清空redis消息队列
     */
    void clearQueueValue(Long sku);


    /**
     * 更新库存
     * @param sku
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 清空库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);

    /**
     * 保存下抽奖单聚合对象
     * @param createPartakeOrderAggregate
     */
    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    /**
     * 查询抽奖活动账户实体
     * @param userId
     * @param activityId
     * @return
     */
    RaffleActivityAccountEntity queryRaffleActivityAccount(String userId, Long activityId);


    /**
     * 查询账户活动月记录实体
     * @param userId
     * @param activityId
     * @param month
     * @return
     */
    RaffleActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month);

    /**
     * 查询账户活动日记录实体
     * @param userId
     * @param activityId
     * @param day
     * @return
     */
    RaffleActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day);

    /**
     * 查找未使用抽奖单
     * @param partakeRaffleEntity
     * @return
     */
    UserRaffleOrderEntity queryNoUseRaffleOrder(PartakeRaffleEntity partakeRaffleEntity);

    /**
     * 查询活动的所有sku
     * @param activityId
     * @return
     */
    List<RaffleActivitySkuEntity> queryActivitySkuList(Long activityId);

    /**
     * 查询某个用户在某活动的已抽奖次数
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return
     */
    Integer queryCompletedDrawCount(String userId, Long activityId);

    /**
     * 查询重复sku订单
     * @param outBusinessNo
     * @return
     */
    RaffleActivityOrderEntity queryRaffleActivityOrder(String userId ,String outBusinessNo);

    /**
     * 更新支付订单wait_pay->complete
     * @param deliveryOrderEntity
     */
    void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity);
}
