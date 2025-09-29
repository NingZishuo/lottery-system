package com.lottery.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.lottery.domain.credit.model.aggregate.TradeAggregate;
import com.lottery.domain.credit.model.entity.CreditAccountEntity;
import com.lottery.domain.credit.model.entity.CreditOrderEntity;
import com.lottery.domain.credit.model.entity.TaskEntity;
import com.lottery.domain.credit.model.valobj.AccountStatusVO;
import com.lottery.domain.credit.repository.ICreditRepository;
import com.lottery.infrastructure.event.EventPublisher;
import com.lottery.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import com.lottery.infrastructure.persistent.dao.ITaskDao;
import com.lottery.infrastructure.persistent.dao.IUserCreditAccountDao;
import com.lottery.infrastructure.persistent.dao.IUserCreditOrderDao;
import com.lottery.infrastructure.persistent.po.Task;
import com.lottery.infrastructure.persistent.po.UserCreditAccount;
import com.lottery.infrastructure.persistent.po.UserCreditOrder;
import com.lottery.infrastructure.persistent.redis.IRedisService;
import com.lottery.types.common.Constants;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 用户积分仓储实现
 */
@Repository
@Slf4j
public class CreditRepository implements ICreditRepository {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IUserCreditOrderDao userCreditOrderDao;

    @Autowired
    private IUserCreditAccountDao userCreditAccountDao;

    @Autowired
    private IRaffleActivityOrderDao rafleActivityOrderDao;

    @Autowired
    private ITaskDao taskDao;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private IRedisService redisService;

    @Override
    public void doSavePayActivityOrder(TradeAggregate tradeAggregate) {
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        //1.用户积分订单
        UserCreditOrder userCreditOrder = new UserCreditOrder();
        BeanUtils.copyProperties(creditOrderEntity, userCreditOrder);
        userCreditOrder.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrder.setTradeType(creditOrderEntity.getTradeType().getCode());

        //2.用户积分账户
        UserCreditAccount userCreditAccount = new UserCreditAccount();
        BeanUtils.copyProperties(creditAccountEntity, userCreditAccount);
        userCreditAccount.setAvailableAmount(creditAccountEntity.getAdjustAmount());

        //3.task消息
        Task task = new Task();
        BeanUtils.copyProperties(taskEntity, task);
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        String cacheKey = Constants.RedisKey.USER_CREDIT_ACCOUNT_KEY + userCreditOrder.getUserId();
        RLock lock = redisService.getLock(cacheKey);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                boolean tryLock = false;
                try {
                    tryLock = lock.tryLock(10, TimeUnit.SECONDS);
                    if (tryLock) {
                        //1.插入订单
                        userCreditOrderDao.insert(userCreditOrder);
                        //2.更新积分
                        userCreditAccountDao.updateSubtractionAmount(userCreditAccount);
                        //3.写入任务
                        taskDao.insert(task);
                    }
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", creditAccountEntity.getUserId(), creditOrderEntity.getOrderId(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (tryLock) {
                        lock.unlock();
                    }
                }
            }
        });

        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getQueue(), task.getMessage());
            // 更新数据库记录，task 任务表
            taskDao.updateTaskSendMessageCompleted(task);
            log.info("调整账户积分记录，发送MQ消息完成 userId: {} orderId:{} queueName: {}", creditAccountEntity.getUserId(), creditOrderEntity.getOrderId(), task.getQueue());
        } catch (Exception e) {
            log.error("调整账户积分记录，发送MQ消息失败 userId: {} queueName: {}", creditAccountEntity.getUserId(), task.getQueue());
            taskDao.updateTaskSendMessageFail(task);
        }
    }


    @Override
    public void doNoPayCreditOrder(TradeAggregate tradeAggregate) {
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();

        //1.用户积分订单
        UserCreditOrder userCreditOrder = new UserCreditOrder();
        BeanUtils.copyProperties(creditOrderEntity, userCreditOrder);
        userCreditOrder.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrder.setTradeType(creditOrderEntity.getTradeType().getCode());

        //2.用户积分账户
        UserCreditAccount userCreditAccount = new UserCreditAccount();
        BeanUtils.copyProperties(creditAccountEntity, userCreditAccount);
        userCreditAccount.setTotalAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccount.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccount.setAccountStatus(AccountStatusVO.OPEN.getCode());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    //1.插入订单
                    userCreditOrderDao.insert(userCreditOrder);
                    //2.更新积分 || 创建积分账户
                    int updateAccountCount = userCreditAccountDao.updateAddAmount(userCreditAccount);
                    if (0 == updateAccountCount) {
                        userCreditAccountDao.insert(userCreditAccount);
                    }
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", creditAccountEntity.getUserId(), creditOrderEntity.getOrderId(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            }
        });

    }

    /**
     * 用户积分是否足够
     *
     * @param userId
     * @param outBusinessNo
     * @return
     */
    @Override
    public Boolean creditIsEnough(String userId, String outBusinessNo) {

        String cacheKey = Constants.RedisKey.USER_CREDIT_ACCOUNT_KEY + userId;

        RLock lock = redisService.getLock(cacheKey);

        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(10, TimeUnit.SECONDS);
            if (tryLock) {
                //获取用户剩余积分
                BigDecimal availableAmount = userCreditAccountDao.queryCreditAccountAvailableAmount(userId);
                if (availableAmount == null){
                    //不存在该账户 直接返回false
                    return false;
                }
                //获取sku订单所需支付积分
                BigDecimal payAmount = rafleActivityOrderDao.queryActivityOrderPayAmount(userId, outBusinessNo);
                if (availableAmount.compareTo(payAmount) < 0) {
                    return false;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (tryLock) {
                lock.unlock();
            }
        }
        return true;
    }

    /**
     * 获取用户剩余积分
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal queryCreditAccountAvailableAmount(String userId) {
        return userCreditAccountDao.queryCreditAccountAvailableAmount(userId);
    }


    /**
     * 获取raffle_activity_order某个订单所需支付积分金额
     */
    @Override
    public BigDecimal queryActivityOrderPayAmount(String userId, String outBusinessNo) {
        return rafleActivityOrderDao.queryActivityOrderPayAmount(userId, outBusinessNo);
    }


}
