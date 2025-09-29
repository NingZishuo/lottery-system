package com.lottery.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.lottery.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.lottery.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.lottery.domain.rebate.model.entity.TaskEntity;
import com.lottery.domain.rebate.model.valobj.BehaviorTypeVO;
import com.lottery.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.lottery.domain.rebate.model.valobj.RebateTypeVO;
import com.lottery.domain.rebate.repository.IBehaviorRebateRepository;
import com.lottery.infrastructure.event.EventPublisher;
import com.lottery.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import com.lottery.infrastructure.persistent.dao.ITaskDao;
import com.lottery.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import com.lottery.infrastructure.persistent.po.DailyBehaviorRebate;
import com.lottery.infrastructure.persistent.po.Task;
import com.lottery.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 行为返利服务仓储实现
 */
@Slf4j
@Component
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDailyBehaviorRebateDao dailyBehaviorRebateDao;

    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;

    @Resource
    private ITaskDao taskDao;

    @Resource
    private EventPublisher eventPublisher;

    /**
     * 查询是否存在对应的日行为返利配置
     *
     * @param behaviorTypeVO
     * @return
     */
    @Override
    public List<DailyBehaviorRebateVO> queryDailyBehaviorRebate(BehaviorTypeVO behaviorTypeVO) {
        List<DailyBehaviorRebate> dailyBehaviorRebateList = dailyBehaviorRebateDao.queryDailyBehaviorRebate(DailyBehaviorRebate.builder()
                .behaviorType(behaviorTypeVO.getCode())
                .build());

        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOList = new ArrayList<>();
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebateList) {
            DailyBehaviorRebateVO dailyBehaviorRebateVO = new DailyBehaviorRebateVO();
            BeanUtils.copyProperties(dailyBehaviorRebate, dailyBehaviorRebateVO);
            dailyBehaviorRebateVO.setBehaviorTypeVO(BehaviorTypeVO.valueOfLowerCase(dailyBehaviorRebate.getBehaviorType()));
            dailyBehaviorRebateVO.setRebateTypeVO(RebateTypeVO.valueOfLowerCase(dailyBehaviorRebate.getRebateType()));
            dailyBehaviorRebateVOList.add(dailyBehaviorRebateVO);
        }

        return dailyBehaviorRebateVOList;
    }

    /**
     * 保存聚合对象
     *
     * @param behaviorRebateAggregates
     */
    @Override
    public void saveUserRebateRecord(List<BehaviorRebateAggregate> behaviorRebateAggregates) {
        for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
            BeanUtils.copyProperties(behaviorRebateOrderEntity, userBehaviorRebateOrder);
            userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorTypeVO().getCode());
            userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateTypeVO().getCode());


            Task task = new Task();
            BeanUtils.copyProperties(taskEntity, task);
            task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
            task.setState(taskEntity.getState().getCode());

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        log.info("写入行为返利记录userId: {} outBusinessId:{}", userBehaviorRebateOrder.getUserId(), userBehaviorRebateOrder.getOutBusinessNo());
                        userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);
                        taskDao.insert(task);
                    } catch (DuplicateKeyException e) {
                        status.setRollbackOnly();
                        log.error("写入行为返利记录，唯一索引冲突 userId: {} outBusinessId:{}", userBehaviorRebateOrder.getUserId(), userBehaviorRebateOrder.getOutBusinessNo(), e);
                        throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                    }
                }
            });
            //发送mq消息
            try {
                log.info("写入返利记录，发送MQ消息 queueName: {} message:{}",task.getQueue() ,taskEntity.getMessage());
                // 发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(taskEntity.getQueue(), taskEntity.getMessage());
                // 更新数据库记录，task 任务表
                taskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("写入返利记录，发送MQ消息失败  queueName: {} message:{}",task.getQueue() ,taskEntity.getMessage());
                taskDao.updateTaskSendMessageFail(task);
            }
        }
    }

    /**
     * 通过防重ID查询是否已经存在对应返利订单
     *
     * @param userId
     * @param outBusinessNoList
     * @return
     */
    @Override
    public List<BehaviorRebateOrderEntity> queryUserRebateOrderByOutBusinessNoList(String userId, List<String> outBusinessNoList) {
       List<UserBehaviorRebateOrder> userBehaviorRebateOrderList = userBehaviorRebateOrderDao.queryRebateOrderByoutBusinessNoList(userId,outBusinessNoList);
       if (userBehaviorRebateOrderList == null || userBehaviorRebateOrderList.isEmpty()) {
           return new ArrayList<>();
       }
        List<BehaviorRebateOrderEntity> behaviorRebateOrderEntityList = new ArrayList<>();
        for (UserBehaviorRebateOrder userBehaviorRebateOrder : userBehaviorRebateOrderList) {
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = new BehaviorRebateOrderEntity();
            BeanUtils.copyProperties(userBehaviorRebateOrder, behaviorRebateOrderEntity);
            behaviorRebateOrderEntity.setBehaviorTypeVO(BehaviorTypeVO.valueOfLowerCase(userBehaviorRebateOrder.getBehaviorType()));
            behaviorRebateOrderEntity.setRebateTypeVO(RebateTypeVO.valueOfLowerCase(userBehaviorRebateOrder.getRebateType()));
            behaviorRebateOrderEntityList.add(behaviorRebateOrderEntity);
        }

        return behaviorRebateOrderEntityList;
    }
}
