package com.lottery.domain.activity.service.partake;

import com.lottery.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.lottery.domain.activity.model.entity.PartakeRaffleEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityEntity;
import com.lottery.domain.activity.model.entity.UserRaffleOrderEntity;
import com.lottery.domain.activity.model.valobj.ActivityStateVO;
import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.IRaffleActivityPartakeService;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 抽奖单获取抽象类
 */
@Slf4j
public abstract class AbstractRaffleActivityAccountPartakeService implements IRaffleActivityPartakeService {

    @Autowired
    protected IActivityRepository activityRepository;

    /**
     * 使用抽奖机会->创建抽奖单->使用抽奖单抽奖->存在未使用抽奖单则用之前未使用的抽奖单
     *
     * @param partakeRaffleEntity
     * @return 用户抽奖单
     */
    @Override
    public UserRaffleOrderEntity createPartakeOrder(PartakeRaffleEntity partakeRaffleEntity) {
        //0.基础信息获取
        String userId = partakeRaffleEntity.getUserId();
        Long activityId = partakeRaffleEntity.getActivityId();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        if (userId.isEmpty() || activityId == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //1.活动查询
        RaffleActivityEntity raffleActivityEntity = activityRepository.queryRaffleActivity(activityId);
        //1.1是否存在该活动
        if (raffleActivityEntity == null) {
            throw new AppException(ResponseCode.ACTIVITY_IS_NULL.getCode(), ResponseCode.ACTIVITY_IS_NULL.getInfo());
        }
        //1.2活动是否开启
        if (!ActivityStateVO.open.equals(raffleActivityEntity.getState())) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        //1.3 活动是否不在时间内
        // 校验；活动日期「开始时间 <- 当前时间 -> 结束时间」
        if (raffleActivityEntity.getEndDateTime().isBefore(now) || raffleActivityEntity.getBeginDateTime().isAfter(now)) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        //2.查询未被使用的抽奖单
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUseRaffleOrder(partakeRaffleEntity);
        if (userRaffleOrderEntity != null) {
            log.info("存在未消费的抽奖单:userRaffleOrderEntity={}", userRaffleOrderEntity);
            return userRaffleOrderEntity;
        }
        //3.判断账户额度是否足够 同时构建下(抽奖)单聚合对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilterAccount(userId, activityId, now,partakeRaffleEntity.getRaffleType());

        //4.构建订单实体对象
        userRaffleOrderEntity = this.buildUserRaffleOrder(userId, activityId, now,partakeRaffleEntity.getRaffleType());
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrderEntity);
        //6.保存聚合对象
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);


        return userRaffleOrderEntity;
    }

    /**
     * 重载而已
     *
     * @param userId
     * @param activityId
     */
    @Override
    public UserRaffleOrderEntity createPartakeOrder(String userId, Long activityId, RaffleTypeVO raffleType) {
        return this.createPartakeOrder(PartakeRaffleEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .raffleType(raffleType)
                .build());
    }

    /**
     * 过滤账户额度 同时判断是否
     *
     * @param userId
     * @param activityId
     * @param now
     * @return
     */
    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, LocalDateTime now,RaffleTypeVO raffleType);

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, LocalDateTime now, RaffleTypeVO raffleType);

}
