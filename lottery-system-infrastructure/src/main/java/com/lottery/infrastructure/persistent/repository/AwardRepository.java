package com.lottery.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.lottery.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.lottery.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.lottery.domain.award.model.entity.AwardEntity;
import com.lottery.domain.award.model.entity.CreditAccountEntity;
import com.lottery.domain.award.model.entity.TaskEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;
import com.lottery.domain.award.model.valobj.AccountStatusVO;
import com.lottery.domain.award.model.valobj.AwardStateVO;
import com.lottery.domain.award.repository.IAwardRepository;
import com.lottery.infrastructure.event.EventPublisher;
import com.lottery.infrastructure.persistent.dao.*;
import com.lottery.infrastructure.persistent.po.*;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 中奖(发奖)相关仓储实现
 */
@Repository
@Slf4j
@EnableAsync
public class AwardRepository implements IAwardRepository {

    @Autowired
    private ITaskDao taskDao;

    @Autowired
    private IUserAwardRecordDao userAwardRecordDao;

    @Autowired
    private IUserRaffleOrderDao userRaffleOrderDao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private IAwardDao awardDao;

    @Autowired
    private IUserCreditAccountDao userCreditAccountDao;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Async
    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        //1.获取UserAwardRecord数据库操作对象
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        BeanUtils.copyProperties(userAwardRecordEntity, userAwardRecord);
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        //2.获取Task数据库操作对象
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        Task task = new Task();
        BeanUtils.copyProperties(taskEntity, task);
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        //3.插入数据库对应记录
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    log.info("写入中奖记录 userId: {} activityId: {} awardId: {} awardConfig:{}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), userAwardRecord.getAwardConfig());
                    //报废抽奖单 将抽奖单设为已使用过
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(UserRaffleOrder.builder()
                            .orderId(userAwardRecord.getOrderId())
                            .build());
                    if (count == 0) {
                        log.error("写入中奖记录 该抽奖单已被使用 userId: {} activityId: {} awardId: {} awardConfig:{} ,orderId:{}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), userAwardRecord.getAwardConfig(), userAwardRecord.getOrderId());
                        status.setRollbackOnly();
                        throw new AppException(ResponseCode.USER_RAFFLE_ORDER_ERROR.getCode(), ResponseCode.USER_RAFFLE_ORDER_ERROR.getInfo());
                    }
                    //写入记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {} awardConfig:{}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), userAwardRecord.getAwardConfig(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
            }
        });
        try {
            log.info("写入中奖记录，发送MQ消息 userId: {} queue: {}", userAwardRecord.getUserId(), task.getQueue());
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getQueue(), task.getMessage());
            // 发送成功,task的state改为completed
            taskDao.updateTaskSendMessageCompleted(task);
        } catch (Exception e) {
            log.error("写入中奖记录，发送MQ消息失败 userId: {} queue: {}", userAwardRecord.getUserId(), task.getQueue());
            taskDao.updateTaskSendMessageFail(task);
        }
    }

    @Async
    @Override
    public void saveUserAwardRecordList(List<UserAwardRecordAggregate> userAwardRecordAggregateList) {
        //3.插入数据库对应记录
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                String orderId = userAwardRecordAggregateList.get(0).getUserAwardRecordEntity().getOrderId();
                //报废抽奖单 将抽奖单设为已使用过
                int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(UserRaffleOrder.builder()
                        .orderId(orderId)
                        .build());
                if (count == 0) {
                    log.error("写入中奖记录 该抽奖单已被使用 orderId:{}", orderId);
                    status.setRollbackOnly();
                    throw new AppException(ResponseCode.USER_RAFFLE_ORDER_ERROR.getCode(), ResponseCode.USER_RAFFLE_ORDER_ERROR.getInfo());
                }
                for (int i = 0; i < userAwardRecordAggregateList.size(); i++) {
                    UserAwardRecordAggregate userAwardRecordAggregate = userAwardRecordAggregateList.get(i);
                    //1.获取UserAwardRecord数据库操作对象
                    UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
                    UserAwardRecord userAwardRecord = new UserAwardRecord();
                    BeanUtils.copyProperties(userAwardRecordEntity, userAwardRecord);
                    userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());
                    userAwardRecord.setOrderId(orderId + "_" + i);

                    //2.获取Task数据库操作对象
                    TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
                    Task task = new Task();
                    BeanUtils.copyProperties(taskEntity, task);
                    task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                    task.setState(taskEntity.getState().getCode());
                    try {
                        log.info("写入中奖记录 userId: {} activityId: {} awardId: {} awardConfig:{}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), userAwardRecord.getAwardConfig());
                        //写入记录
                        userAwardRecordDao.insert(userAwardRecord);
                        //写入任务
                        taskDao.insert(task);
                    } catch (DuplicateKeyException e) {
                        status.setRollbackOnly();
                        log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {} awardConfig:{}", userAwardRecord.getUserId(), userAwardRecord.getActivityId(), userAwardRecord.getAwardId(), userAwardRecord.getAwardConfig(), e);
                        throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                    }
                    try {
                        log.info("写入中奖记录，发送MQ消息 userId: {} queue: {}", userAwardRecord.getUserId(), task.getQueue());
                        // 发送消息【在事务外执行，如果失败还有任务补偿】
                        eventPublisher.publish(task.getQueue(), task.getMessage());
                        // 发送成功,task的state改为completed
                        taskDao.updateTaskSendMessageCompleted(task);
                    } catch (Exception e) {
                        log.error("写入中奖记录，发送MQ消息失败 userId: {} queue: {}", userAwardRecord.getUserId(), task.getQueue());
                        taskDao.updateTaskSendMessageFail(task);
                    }
                }
            }
        });
    }

    @Override
    public AwardEntity queryAward(Long awardId) {
        Award award = awardDao.queryAward(Award.builder()
                .awardId(awardId)
                .build());
        if (award == null) {
            return null;
        }
        AwardEntity awardEntity = new AwardEntity();
        BeanUtils.copyProperties(award, awardEntity);
        return awardEntity;
    }


    @Override
    public void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizesAggregate.getUserAwardRecordEntity();
        CreditAccountEntity creditAccountEntity = giveOutPrizesAggregate.getCreditAccountEntity();

        //获取与数据库操作的对象
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        BeanUtils.copyProperties(userAwardRecordEntity, userAwardRecord);

        //获取与数据库操作的对象
        UserCreditAccount userCreditAccount = UserCreditAccount.builder()
                .userId(creditAccountEntity.getUserId())
                .totalAmount(creditAccountEntity.getRandomCredit())
                .availableAmount(creditAccountEntity.getRandomCredit())
                .accountStatus(AccountStatusVO.open.getCode())
                .build();

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    // 更新奖品记录
                    int updateAwardCount = userAwardRecordDao.updateAwardRecordCompletedState(userAwardRecord);
                    if (0 == updateAwardCount) {
                        log.error("更新中奖记录，重复更新拦截 giveOutPrizesAggregate:{}", JSON.toJSONString(giveOutPrizesAggregate));
                        status.setRollbackOnly();
                        throw new AppException(ResponseCode.REPEAT_DISTRIBUTE_AWARD.getCode(), ResponseCode.REPEAT_DISTRIBUTE_AWARD.getInfo());
                    }

                    // 更新积分 || 创建积分账户
                    int updateAccountCount = userCreditAccountDao.updateAddAmount(userCreditAccount);
                    if (0 == updateAccountCount) {
                        userCreditAccountDao.insert(userCreditAccount);
                    }
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新中奖记录，唯一索引冲突 userId: {} ", creditAccountEntity.getUserId(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            }
        });

    }

    @Override
    public List<UserAwardRecordEntity> queryUserAwardRecordList(String userId, Long activityId) {
        List<UserAwardRecord> userAwardRecords = userAwardRecordDao.queryUserAwardRecordList(UserAwardRecord.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        List<UserAwardRecordEntity> userAwardRecordEntityList = new ArrayList<>();
        for (UserAwardRecord userAwardRecord : userAwardRecords) {
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            BeanUtils.copyProperties(userAwardRecord, userAwardRecordEntity);
            userAwardRecordEntity.setAwardState(AwardStateVO.valueOf(userAwardRecord.getAwardState()));
            userAwardRecordEntityList.add(userAwardRecordEntity);
        }
        return userAwardRecordEntityList;
    }
}
