package com.lottery.infrastructure.persistent.repository;

import com.lottery.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.lottery.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.lottery.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.lottery.domain.activity.model.valobj.ActivityStateVO;
import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import com.lottery.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.infrastructure.event.EventPublisher;
import com.lottery.infrastructure.persistent.dao.*;
import com.lottery.infrastructure.persistent.po.*;
import com.lottery.infrastructure.persistent.redis.IRedisService;
import com.lottery.types.common.Constants;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 活动仓储实现
 */
@Repository
@Slf4j
public class ActivityRepository implements IActivityRepository {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private IRaffleActivityDao raffleActivityDao;

    @Autowired
    private IRaffleActivitySkuDao raffleActivitySkuDao;

    @Autowired
    private IRaffleActivityCountDao raffleActivityCountDao;

    @Autowired
    private IRaffleActivityAccountDao raffleActivityAccountDao;

    @Autowired
    private IRaffleActivityAccountMonthDao raffleActivityAccountMonthDao;

    @Autowired
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Autowired
    private IRaffleActivityOrderDao raffleActivityOrderDao;

    @Autowired
    private IUserRaffleOrderDao userRaffleOrderDao;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;


    /**
     * 查询活动sku实体对象
     *
     * @param sku 标识符
     * @return
     */
    @Override
    public RaffleActivitySkuEntity queryActivitySku(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_KEY + sku;

        RaffleActivitySkuEntity raffleActivitySkuEntity = redisService.getValue(cacheKey);
        if (raffleActivitySkuEntity != null) {
            return raffleActivitySkuEntity;
        }

        RaffleActivitySku raffleActivitySku = RaffleActivitySku
                .builder()
                .sku(sku)
                .build();

        raffleActivitySku = raffleActivitySkuDao.queryRaffleActivitySku(raffleActivitySku);
        if (raffleActivitySku == null) {
            return null;
        }

        raffleActivitySkuEntity = new RaffleActivitySkuEntity();
        BeanUtils.copyProperties(raffleActivitySku, raffleActivitySkuEntity);
        redisService.setValue(cacheKey, raffleActivitySkuEntity);

        return raffleActivitySkuEntity;
    }

    @Override
    public List<RaffleActivitySkuEntity> queryActivitySkuList(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkuList = raffleActivitySkuDao.queryRaffleActivitySkuList(RaffleActivitySku
                .builder()
                .activityId(activityId)
                .build());

        List<RaffleActivitySkuEntity> raffleActivitySkuEntityList = new ArrayList<>(raffleActivitySkuList.size());

        for (RaffleActivitySku raffleActivitySku : raffleActivitySkuList) {
            RaffleActivitySkuEntity raffleActivitySkuEntity = new RaffleActivitySkuEntity();
            BeanUtils.copyProperties(raffleActivitySku, raffleActivitySkuEntity);
            raffleActivitySkuEntityList.add(raffleActivitySkuEntity);
        }

        return raffleActivitySkuEntityList;
    }

    /**
     * 查询某个用户在某活动的已抽奖次数
     *
     * @param userId     用户ID
     * @param activityId 活动ID
     * @return
     */
    @Override
    public Integer queryCompletedDrawCount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryRaffleActivityAccount(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        //如果查询不到用户参与活动信息 就是0
        if (raffleActivityAccount == null) {
            return 0;
        }
        //总次数-剩余次数 = 总的已抽奖次数
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

    @Override
    public RaffleActivityOrderEntity queryRaffleActivityOrder(String userId, String outBusinessNo) {
        RaffleActivityOrder raffleActivityOrder = raffleActivityOrderDao.queryRaffleActivityOrder(RaffleActivityOrder.builder()
                .userId(userId)
                .outBusinessNo(outBusinessNo)
                .build());

        if (raffleActivityOrder == null) {
            return null;
        }
        RaffleActivityOrderEntity raffleActivityOrderEntity = new RaffleActivityOrderEntity();
        BeanUtils.copyProperties(raffleActivityOrder, raffleActivityOrderEntity);

        return raffleActivityOrderEntity;
    }

    /**
     * 更新支付订单wait_pay->complete
     *
     * @param deliveryOrderEntity
     */
    @Override
    public void updateActivityOrder(DeliveryOrderEntity deliveryOrderEntity) {

        LocalDateTime now = LocalDateTime.now();
        String formatMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String formatDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //充值订单记录
        RaffleActivityOrder raffleActivityOrder = raffleActivityOrderDao.queryRaffleActivityOrder(RaffleActivityOrder.builder()
                .userId(deliveryOrderEntity.getUserId())
                .outBusinessNo(deliveryOrderEntity.getOutBusinessNo())
                .build());
        //说明不存在该sku订单 或者 该sku已被使用
        if (raffleActivityOrder == null) {
            throw new AppException(ResponseCode.REPEAT_DISTRIBUTE_SKU.getCode(), ResponseCode.REPEAT_DISTRIBUTE_SKU.getInfo());
        }

        //账户充值
        RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                .userId(raffleActivityOrder.getUserId())
                .activityId(raffleActivityOrder.getActivityId())
                .totalCount(raffleActivityOrder.getTotalCount())
                .totalCountSurplus(raffleActivityOrder.getTotalCount())
                .build();
        //账户月充值
        RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                .userId(raffleActivityOrder.getUserId())
                .activityId(raffleActivityOrder.getActivityId())
                .month(formatMonth)
                .monthCount(raffleActivityOrder.getTotalCount())
                .monthCountSurplus(raffleActivityOrder.getTotalCount())
                .build();
        //账户日充值
        RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                .userId(raffleActivityOrder.getUserId())
                .activityId(raffleActivityOrder.getActivityId())
                .day(formatDay)
                .dayCount(raffleActivityOrder.getTotalCount())
                .dayCountSurplus(raffleActivityOrder.getTotalCount())
                .build();


        //注意 与数据库的交互还是得数据库对象
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    //1.更新订单
                    int result = raffleActivityOrderDao.updateRaffleActivityOrderCompleted(RaffleActivityOrder.builder()
                            .userId(deliveryOrderEntity.getUserId())
                            .orderId(deliveryOrderEntity.getOrderId())
                            .outBusinessNo(deliveryOrderEntity.getOutBusinessNo())
                            .build());
                    //2.说明不存在该sku订单 或者 该sku已被使用
                    if (result == 0) {
                        throw new AppException(ResponseCode.REPEAT_DISTRIBUTE_SKU.getCode(), ResponseCode.REPEAT_DISTRIBUTE_SKU.getInfo());
                    }
                    //2.更新账户
                    int count = raffleActivityAccountDao.addAccountQuota(raffleActivityAccount);
                    //2.1.说明不存在主账户
                    if (count == 0) {
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    //4.更新月账户 不存在也不需要创建
                    raffleActivityAccountMonthDao.addAccountMonthQuota(raffleActivityAccountMonth);

                    //5.更新日账户 不存在也不需要创建
                    raffleActivityAccountDayDao.addAccountDayQuota(raffleActivityAccountDay);
                } catch (DuplicateKeyException e) {
                    //其实这里就回滚了  当然你可以不写 让spring给你回滚
                    status.setRollbackOnly();
                    log.info("活动账户唯一索引冲突,userId:{} activityId:{} , sku:{}", raffleActivityAccount.getUserId(), raffleActivityAccount.getActivityId(), raffleActivityOrder.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            }
        });
    }

    /**
     * 查询活动实体对象
     *
     * @param activityId
     * @return
     */
    @Override
    public RaffleActivityEntity queryRaffleActivity(Long activityId) {

        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;

        RaffleActivityEntity raffleActivityEntity = redisService.getValue(cacheKey);
        if (raffleActivityEntity != null) {
            return raffleActivityEntity;
        }

        RaffleActivity raffleActivity = RaffleActivity
                .builder()
                .activityId(activityId)
                .build();

        raffleActivity = raffleActivityDao.queryRaffleActivity(raffleActivity);
        if (raffleActivity == null) {
            return null;
        }
        raffleActivityEntity = new RaffleActivityEntity();
        BeanUtils.copyProperties(raffleActivity, raffleActivityEntity);
        raffleActivityEntity.setState(ActivityStateVO.valueOf(raffleActivity.getState()));
        redisService.setValue(cacheKey, raffleActivityEntity);
        return raffleActivityEntity;
    }

    /**
     * 查询活动次数实体对象
     *
     * @param activityCountId
     * @return
     */
    @Override
    public RaffleActivityCountEntity queryRaffleActivityCount(Long activityCountId) {
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;

        RaffleActivityCountEntity raffleActivityCountEntity = redisService.getValue(cacheKey);
        if (raffleActivityCountEntity != null) {
            return raffleActivityCountEntity;
        }

        RaffleActivityCount raffleActivityCount = RaffleActivityCount
                .builder()
                .activityCountId(activityCountId)
                .build();

        raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCount(raffleActivityCount);
        if (raffleActivityCount == null) {
            return null;
        }

        raffleActivityCountEntity = new RaffleActivityCountEntity();
        BeanUtils.copyProperties(raffleActivityCount, raffleActivityCountEntity);

        redisService.setValue(cacheKey, raffleActivityCountEntity);

        return raffleActivityCountEntity;
    }


    @Override
    public void doSaveNoPayActivityOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {

        RaffleActivityOrderEntity raffleActivityOrderEntity = createQuotaOrderAggregate.getRaffleActivityOrderEntity();
        RaffleActivityAccountEntity raffleActivityAccountEntity = createQuotaOrderAggregate.getRaffleActivityAccountEntity();
        RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = createQuotaOrderAggregate.getRaffleActivityAccountMonthEntity();
        RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = createQuotaOrderAggregate.getRaffleActivityAccountDayEntity();

        //账户充值
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        BeanUtils.copyProperties(raffleActivityAccountEntity, raffleActivityAccount);
        //账户月充值
        RaffleActivityAccountMonth raffleActivityAccountMonth = new RaffleActivityAccountMonth();
        BeanUtils.copyProperties(raffleActivityAccountMonthEntity, raffleActivityAccountMonth);
        //账户日充值
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        BeanUtils.copyProperties(raffleActivityAccountDayEntity, raffleActivityAccountDay);

        //充值订单记录
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        BeanUtils.copyProperties(raffleActivityOrderEntity, raffleActivityOrder);
        raffleActivityOrder.setState(raffleActivityOrderEntity.getState().getCode());

        //注意 与数据库的交互还是得数据库对象
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    //1.插入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                    //2.更新账户
                    int count = raffleActivityAccountDao.addAccountQuota(raffleActivityAccount);
                    //2.1.说明不存在主账户
                    if (count == 0) {
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    //4.更新月账户 不存在也不需要创建
                    raffleActivityAccountMonthDao.addAccountMonthQuota(raffleActivityAccountMonth);

                    //5.更新日账户 不存在也不需要创建
                    raffleActivityAccountDayDao.addAccountDayQuota(raffleActivityAccountDay);
                } catch (DuplicateKeyException e) {
                    //其实这里就回滚了  当然你可以不写 让spring给你回滚
                    status.setRollbackOnly();
                    log.info("活动账户唯一索引冲突,userId:{} activityId:{} , sku:{}", raffleActivityAccountEntity.getUserId(), raffleActivityAccountEntity.getActivityId(), raffleActivityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            }
        });


    }

    /**
     * sku充值账户 - 购买
     *
     * @param createQuotaOrderAggregate
     */
    @Override
    public void doSavePayActivityOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        RaffleActivityOrderEntity raffleActivityOrderEntity = createQuotaOrderAggregate.getRaffleActivityOrderEntity();

        //充值订单记录
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        BeanUtils.copyProperties(raffleActivityOrderEntity, raffleActivityOrder);
        raffleActivityOrder.setState(raffleActivityOrderEntity.getState().getCode());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    //1.插入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);
                } catch (DuplicateKeyException e) {
                    //其实这里就回滚了  当然你可以不写 让spring给你回滚
                    status.setRollbackOnly();
                    log.info("sku支付订单唯一索引冲突,userId:{} activityId:{} , sku:{}", raffleActivityOrderEntity.getUserId(), raffleActivityOrderEntity.getActivityId(), raffleActivityOrderEntity.getSku());
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            }
        });
    }

    /**
     * 缓存sku的库存
     *
     * @param activityId
     * @param sku
     * @param stockCount
     */
    @Override
    public void cacheActivitySkuStockCount(Long activityId, Long sku, Integer stockCount) {
        String key = Constants.RedisKey.ACTIVITY_SKU_COUNT_KEY + activityId + ":" + sku;
        //redis有就不需要再次填入库存
        if (redisService.isExists(key)) {
            return;
        }
        redisService.setAtomicLong(key, stockCount);

    }

    @Override
    public boolean subtractionActivitySkuStock(Long activityId, Long sku, LocalDateTime endDateTime) {
        String key = Constants.RedisKey.ACTIVITY_SKU_COUNT_KEY + activityId + ":" + sku;
        long surplus = redisService.decr(key);
        if (surplus == 0) {
            String queueName = activitySkuStockZeroMessageEvent.queueName();
            BaseEvent.EventMessage<Long> eventMessage = activitySkuStockZeroMessageEvent.buildEventMessage(sku);
            eventPublisher.publish(queueName, eventMessage);
        } else if (surplus < 0) {
            redisService.setValue(key, 0);
            return false;
        }
        //这里加锁 让每个awardId的每一个库存只能卖出去一次 间接说明这种写法是不支持补货的
        String lockKey = key + ":" + surplus;
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        long second = endDateTime.atZone(zoneId).toEpochSecond() - LocalDateTime.now().atZone(zoneId).toEpochSecond() + TimeUnit.DAYS.toSeconds(1);
        Boolean status = redisService.setNx(lockKey, second, TimeUnit.SECONDS);
        if (!status) {
            log.info("活动sku加锁失败:{}", lockKey);
        }

        return status;
    }

    /**
     * 发送消息队列扣减库存
     *
     * @param activitySkuStockKeyVO
     */
    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String keyTotal = Constants.RedisKey.ACTIVITY_SKU_BLOCK_QUEUE_TOTAL_KEY;
        RBlockingQueue<Long> blockingQueueTotal = redisService.getBlockingQueue(keyTotal);
        RDelayedQueue<Long> delayedQueueTotal = redisService.getDelayedQueue(blockingQueueTotal);
        delayedQueueTotal.offer(activitySkuStockKeyVO.getSku(), 3, TimeUnit.SECONDS);

        String key = Constants.RedisKey.ACTIVITY_SKU_BLOCK_QUEUE_KEY + activitySkuStockKeyVO.getSku();
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 1, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue(Long sku) {
        String key = Constants.RedisKey.ACTIVITY_SKU_BLOCK_QUEUE_KEY + sku;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        return blockingQueue.poll();
    }

    /**
     * 获取总的redis消息队列
     *
     * @return
     */
    @Override
    public Long takeTotalQueueValue() {
        String key = Constants.RedisKey.ACTIVITY_SKU_BLOCK_QUEUE_TOTAL_KEY;
        RBlockingQueue<Long> blockingQueue = redisService.getBlockingQueue(key);
        return blockingQueue.poll();
    }

    /**
     * 清空redis消息队列
     */
    @Override
    public void clearQueueValue(Long sku) {
        String key = Constants.RedisKey.ACTIVITY_SKU_BLOCK_QUEUE_KEY + sku;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.clear();
        blockingQueue.clear();
    }

    /**
     * 更新库存
     *
     * @param sku
     */
    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    /**
     * 清空库存
     *
     * @param sku
     */
    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }


    /**
     * 查询抽奖活动账户实体
     *
     * @param userId
     * @param activityId
     * @return
     */
    @Override
    public RaffleActivityAccountEntity queryRaffleActivityAccount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        raffleActivityAccount = raffleActivityAccountDao.queryRaffleActivityAccount(raffleActivityAccount);
        if (raffleActivityAccount == null) {
            return RaffleActivityAccountEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .totalCount(0)
                    .totalCountSurplus(0)
                    .build();
        }
        RaffleActivityAccountEntity raffleActivityAccountEntity = new RaffleActivityAccountEntity();
        BeanUtils.copyProperties(raffleActivityAccount, raffleActivityAccountEntity);
        return raffleActivityAccountEntity;
    }

    /**
     * 查询账户活动月记录实体
     *
     * @param userId
     * @param activityId
     * @param month
     * @return
     */
    @Override
    public RaffleActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month) {
        RaffleActivityAccountMonth raffleActivityAccountMonth = RaffleActivityAccountMonth.builder()
                .userId(userId)
                .activityId(activityId)
                .month(month)
                .build();

        raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryRaffleActivityAccountMonth(raffleActivityAccountMonth);
        if (raffleActivityAccountMonth == null) {
            return null;
        }
        RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = new RaffleActivityAccountMonthEntity();
        BeanUtils.copyProperties(raffleActivityAccountMonth, raffleActivityAccountMonthEntity);
        return raffleActivityAccountMonthEntity;
    }

    /**
     * 查询账户活动日记录实体
     *
     * @param userId
     * @param activityId
     * @param day
     * @return
     */
    @Override
    public RaffleActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day) {
        RaffleActivityAccountDay raffleActivityAccountDay = RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .day(day)
                .build();

        raffleActivityAccountDay = raffleActivityAccountDayDao.queryRaffleActivityAccountDay(raffleActivityAccountDay);

        if (raffleActivityAccountDay == null) {
            return null;
        }
        RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = new RaffleActivityAccountDayEntity();
        BeanUtils.copyProperties(raffleActivityAccountDay, raffleActivityAccountDayEntity);

        return raffleActivityAccountDayEntity;
    }

    @Override
    public UserRaffleOrderEntity queryNoUseRaffleOrder(PartakeRaffleEntity partakeRaffleEntity) {
        UserRaffleOrder userRaffleOrder = UserRaffleOrder.builder()
                .userId(partakeRaffleEntity.getUserId())
                .activityId(partakeRaffleEntity.getActivityId())
                .raffleType(partakeRaffleEntity.getRaffleType().getCode())
                .orderState("create")
                .build();
        //注意:如果有很多未被使用的抽奖单 则用这个集合中的第一个
        List<UserRaffleOrder> userRaffleOrderList = userRaffleOrderDao.queryNoUseRaffleOrder(userRaffleOrder);
        if (userRaffleOrderList == null || userRaffleOrderList.isEmpty()) {
            return null;
        }
        userRaffleOrder = userRaffleOrderList.get(0);
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        BeanUtils.copyProperties(userRaffleOrder, userRaffleOrderEntity);
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.valueOf(userRaffleOrder.getOrderState()));
        userRaffleOrderEntity.setRaffleType(RaffleTypeVO.valueOf(userRaffleOrder.getRaffleType()));
        return userRaffleOrderEntity;
    }


    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        RaffleActivityAccountEntity raffleActivityAccountEntity = createPartakeOrderAggregate.getRaffleActivityAccountEntity();
        RaffleActivityAccountDayEntity raffleActivityAccountDayEntity = createPartakeOrderAggregate.getRaffleActivityAccountDayEntity();
        RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity = createPartakeOrderAggregate.getRaffleActivityAccountMonthEntity();
        UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

        String userId = raffleActivityAccountEntity.getUserId();
        Long activityId = raffleActivityAccountEntity.getActivityId();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    // 1. 更新总账户
                    int totalCount = raffleActivityAccountDao.updateActivityAccountSpecifiedSubtractionQuota(RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build(),
                            userRaffleOrderEntity.getRaffleType().getSubtractor());
                    if (totalCount == 0) {
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新总账户额度不足，异常 userId: {} activityId: {}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }
                    // 2. 创建或更新月账户
                    if (createPartakeOrderAggregate.getIsExistAccountMonth()) {
                        int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSpecifiedSubtractionQuota(
                                RaffleActivityAccountMonth.builder()
                                        .userId(userId)
                                        .activityId(activityId)
                                        .month(raffleActivityAccountMonthEntity.getMonth())
                                        .build(),
                                userRaffleOrderEntity.getRaffleType().getSubtractor());
                        if (updateMonthCount == 0) {
                            // 未更新成功则回滚
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新月账户额度不足，异常 userId: {} activityId: {} month: {}", userId, activityId, raffleActivityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        raffleActivityAccountMonthDao.insert(RaffleActivityAccountMonth.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .month(raffleActivityAccountMonthEntity.getMonth())
                                .monthCount(raffleActivityAccountMonthEntity.getMonthCount())
                                .monthCountSurplus(raffleActivityAccountMonthEntity.getMonthCountSurplus())
                                .build());
                    }

                    // 3. 创建或更新日账户，true - 存在则更新，false - 不存在则插入
                    if (createPartakeOrderAggregate.getIsExistAccountDay()) {
                        int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDaySpecifiedSubtractionQuota(RaffleActivityAccountDay.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .day(raffleActivityAccountDayEntity.getDay())
                                .build(),
                                userRaffleOrderEntity.getRaffleType().getSubtractor());
                        if (updateDayCount == 0) {
                            // 未更新成功则回滚
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录，更新日账户额度不足，异常 userId: {} activityId: {} day: {}", userId, activityId, raffleActivityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        raffleActivityAccountDayDao.insert(RaffleActivityAccountDay.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .day(raffleActivityAccountDayEntity.getDay())
                                .dayCount(raffleActivityAccountDayEntity.getDayCount())
                                .dayCountSurplus(raffleActivityAccountDayEntity.getDayCountSurplus())
                                .build());
                    }

                    // 4. 写入参与活动订单
                    userRaffleOrderDao.insert(UserRaffleOrder.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .activityName(userRaffleOrderEntity.getActivityName())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .raffleType(userRaffleOrderEntity.getRaffleType().getCode())
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .orderTime(userRaffleOrderEntity.getOrderTime())
                            .orderState(userRaffleOrderEntity.getOrderState().getCode())
                            .build());
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入创建参与活动记录，唯一索引冲突 userId: {} activityId: {}", userId, activityId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            }
        });


    }
}
