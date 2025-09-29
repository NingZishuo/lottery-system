package com.lottery.domain.award.service;

import com.lottery.domain.award.event.SendAwardMessageEvent;
import com.lottery.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.lottery.domain.award.model.entity.*;
import com.lottery.domain.award.model.valobj.TaskStateVO;
import com.lottery.domain.award.repository.IAwardRepository;
import com.lottery.domain.award.service.distribute.IDistributeAward;
import com.lottery.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 中奖(发奖)奖品服务实现类
 */
@Service
@Slf4j
public class AwardService implements IAwardService {


    @Autowired
    private IAwardRepository awardRepository;
    /**
     * 这是消息工具类
     */
    @Autowired
    private SendAwardMessageEvent sendAwardMessageEvent;


    private final Map<String, IDistributeAward> distributeAwardMap;

    @Autowired
    public AwardService(Map<String, IDistributeAward> distributeAwardMap) {
        this.distributeAwardMap = distributeAwardMap;
    }


    /**
     * 保存用户中奖记录
     *
     * @param userAwardRecordEntity
     */
    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //0.创建要发送的消息的主体部分
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .orderId(userAwardRecordEntity.getOrderId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .awardConfig(userAwardRecordEntity.getAwardConfig())
                .build();

        //1.这是要发送的消息
        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        //2.聚合对象所需数据 即可能更改的数据库对象
        TaskEntity taskEntity = TaskEntity.builder()
                .queue(sendAwardMessageEvent.queueName())
                .messageId(sendAwardEventMessage.getMessageId())
                .message(sendAwardEventMessage)
                .state(TaskStateVO.create)
                .build();
        //3.构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .taskEntity(taskEntity)
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();


        //4.保存记录 并且发送mq给发奖平台
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);

    }

    /**
     * 1.批量插入中奖记录 2.报废一张抽奖单
     *
     * @param userAwardRecordEntityList
     */
    @Override
    public void saveUserAwardRecordList(List<UserAwardRecordEntity> userAwardRecordEntityList) {
        List<UserAwardRecordAggregate> userAwardRecordAggregateList = new ArrayList<>(userAwardRecordEntityList.size());
        for (int i = 0; i < userAwardRecordEntityList.size(); i++) {
            UserAwardRecordEntity userAwardRecordEntity = userAwardRecordEntityList.get(i);
            //0.创建要发送的消息的主体部分
            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                    .userId(userAwardRecordEntity.getUserId())
                    .orderId(userAwardRecordEntity.getOrderId() + "_" + i)
                    .awardId(userAwardRecordEntity.getAwardId())
                    .awardTitle(userAwardRecordEntity.getAwardTitle())
                    .awardConfig(userAwardRecordEntity.getAwardConfig())
                    .build();

            //1.这是要发送的消息
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

            //2.聚合对象所需数据 即可能更改的数据库对象
            TaskEntity taskEntity = TaskEntity.builder()
                    .queue(sendAwardMessageEvent.queueName())
                    .messageId(sendAwardEventMessage.getMessageId())
                    .message(sendAwardEventMessage)
                    .state(TaskStateVO.create)
                    .build();
            //3.构建聚合对象
            UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                    .taskEntity(taskEntity)
                    .userAwardRecordEntity(userAwardRecordEntity)
                    .build();

            userAwardRecordAggregateList.add(userAwardRecordAggregate);
        }
        //4.保存记录 并且发送mq给发奖平台
        awardRepository.saveUserAwardRecordList(userAwardRecordAggregateList);
    }

    /**
     * 配送发货奖品
     *
     * @param distributeAwardEntity
     */
    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        Long awardId = distributeAwardEntity.getAwardId();

        AwardEntity awardEntity = awardRepository.queryAward(awardId);

        if (awardEntity == null) {
            log.error("分发奖品，db中奖品不存在。awardId:{}", awardId);
            throw new RuntimeException("分发奖品，db中奖品不存在。awardId:" + awardId);
        }

        IDistributeAward distributeAward = distributeAwardMap.get(awardEntity.getAwardKey());

        if (distributeAward == null) {
            //TODO这里需要补全其它的IDistributeAward实现类
            log.error("分发奖品，对应的服务不存在。awardKey:{}", awardEntity.getAwardKey());
            //throw new RuntimeException("分发奖品，奖品" + awardEntity.getAwardKey() + "对应的服务不存在");
            return;
        }

        distributeAward.giveOutPrizes(distributeAwardEntity);
    }


    /**
     * 显示用户中奖记录 只显示最近的30条
     *
     * @param showAwardRecordEntity
     */
    @Override
    public List<UserAwardRecordEntity> showUserAwardRecordList(ShowAwardRecordEntity showAwardRecordEntity) {
        String userId = showAwardRecordEntity.getUserId();
        Long activityId = showAwardRecordEntity.getActivityId();
        return awardRepository.queryUserAwardRecordList(userId,activityId);
    }
}
