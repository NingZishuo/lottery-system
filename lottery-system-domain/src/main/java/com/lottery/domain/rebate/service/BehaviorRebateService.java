package com.lottery.domain.rebate.service;

import com.lottery.domain.rebate.event.SendRebateMessageEvent;
import com.lottery.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.lottery.domain.rebate.model.entity.BehaviorEntity;
import com.lottery.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.lottery.domain.rebate.model.entity.TaskEntity;
import com.lottery.domain.rebate.model.valobj.BehaviorTypeVO;
import com.lottery.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.lottery.domain.rebate.model.valobj.TaskStateVO;
import com.lottery.domain.rebate.repository.IBehaviorRebateRepository;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.event.BaseEvent;
import com.lottery.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行为返利服务实现
 */
@Service
public class BehaviorRebateService implements IBehaviorRebateService {

    @Autowired
    private IBehaviorRebateRepository behaviorRebateRepository;

    @Autowired
    private SendRebateMessageEvent sendRebateMessageEvent;

    /**
     * 创建用户行为订单
     *
     * @param behaviorEntity
     */
    @Override
    public List<String> createBehaviorRebateOrder(BehaviorEntity behaviorEntity) {
        //0.查询基础信息
        String userId = behaviorEntity.getUserId();
        BehaviorTypeVO behaviorTypeVO = behaviorEntity.getBehaviorTypeVO();
        if (behaviorTypeVO == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //1.查询返利配值
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.queryDailyBehaviorRebate(behaviorEntity.getBehaviorTypeVO());
        if (dailyBehaviorRebateVOS == null || dailyBehaviorRebateVOS.isEmpty()) {
            throw new AppException(ResponseCode.DAILY_BEHAVIOR_REBATE_IS_NULL.getCode(), ResponseCode.DAILY_BEHAVIOR_REBATE_IS_NULL.getInfo());
        }
        //2.构建聚合对象
        //单独处理时间
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //需要返回订单ID
        List<String> orderIds = new ArrayList<>();
        //行为返利聚合对象集合
        List<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>();

        for (DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOS) {
            //创建行为返利订单的聚合对象
            String outBusinessNo = userId + "_" + dailyBehaviorRebateVO.getRebateDesc() + "_" + formatNow;
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(userId)
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorTypeVO(dailyBehaviorRebateVO.getBehaviorTypeVO())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateTypeVO(dailyBehaviorRebateVO.getRebateTypeVO())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .outBusinessNo(outBusinessNo)
                    .build();
            orderIds.add(behaviorRebateOrderEntity.getOrderId());
            //创建mq的聚合对象
            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(userId)
                    .orderId(behaviorRebateOrderEntity.getOrderId())
                    .dailyBehaviorRebateVO(dailyBehaviorRebateVO)
                    .outBusinessNo(outBusinessNo)
                    .build();

            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = sendRebateMessageEvent.buildEventMessage(rebateMessage);

            TaskEntity taskEntity = TaskEntity.builder()
                    .queue(sendRebateMessageEvent.queueName())
                    .messageId(eventMessage.getMessageId())
                    .message(eventMessage)
                    .state(TaskStateVO.create)
                    .build();

            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();

            behaviorRebateAggregates.add(behaviorRebateAggregate);
        }

        //3.存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorRebateAggregates);

        //4.返回订单ID集合
        return orderIds;
    }

    /**
     * 查询某用户是否已经进行某个行为 如签到
     *
     * @param behaviorEntity
     * @return
     */
    @Override
    public List<BehaviorRebateOrderEntity> queryUserRebateOrderByBehaviorType(BehaviorEntity behaviorEntity) {

        String userId = behaviorEntity.getUserId();
        BehaviorTypeVO behaviorTypeVO = behaviorEntity.getBehaviorTypeVO();

        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.queryDailyBehaviorRebate(behaviorTypeVO);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<String> outBusinessNoList = dailyBehaviorRebateVOS.stream()
                .map(dailyBehaviorRebateVO -> userId + "_" + dailyBehaviorRebateVO.getRebateDesc() + "_" + formatNow)
                .collect(Collectors.toList());
        return behaviorRebateRepository.queryUserRebateOrderByOutBusinessNoList(userId, outBusinessNoList);
    }
}
