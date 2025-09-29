package com.lottery.domain.activity.service.partake;

import com.lottery.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import com.lottery.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 抽奖单获取类
 */
@Service
public class DefaultRaffleActivityAccountPartakeService extends AbstractRaffleActivityAccountPartakeService {

    private final DateTimeFormatter dateFormatMonth = DateTimeFormatter.ofPattern("yyyy-MM");

    private final DateTimeFormatter dateFormatDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, LocalDateTime now, RaffleTypeVO raffleType) {
        //1.查询总账户额度
        RaffleActivityAccountEntity raffleActivityAccountEntity = activityRepository.queryRaffleActivityAccount(userId, activityId);
        //1.2不存在额度即报错
        if (raffleActivityAccountEntity == null || raffleActivityAccountEntity.getTotalCountSurplus() < raffleType.getSubtractor()) {
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        String month = now.format(dateFormatMonth);
        String day = now.format(dateFormatDay);
        //2.查询月账户额度
        Boolean isExistAccountMonth = true;
        RaffleActivityAccountMonthEntity raffleactivityAccountMonthEntity = activityRepository.queryActivityAccountMonth(userId, activityId, month);
        if (raffleactivityAccountMonthEntity == null) {
            isExistAccountMonth = false;
            raffleactivityAccountMonthEntity = RaffleActivityAccountMonthEntity.builder()
                    .month(month)
                    .monthCount(raffleActivityAccountEntity.getTotalCountSurplus())
                    .monthCountSurplus(raffleActivityAccountEntity.getTotalCountSurplus() - raffleType.getSubtractor())
                    .build();
        }
        //3.查询日账户额度
        Boolean isExistAccountDay = true;
        RaffleActivityAccountDayEntity raffleactivityAccountDayEntity = activityRepository.queryActivityAccountDay(userId, activityId, day);
        if (raffleactivityAccountDayEntity == null) {
            isExistAccountDay = false;
            raffleactivityAccountDayEntity = RaffleActivityAccountDayEntity.builder()
                    .day(day)
                    .dayCount(raffleActivityAccountEntity.getTotalCountSurplus())
                    .dayCountSurplus(raffleActivityAccountEntity.getTotalCountSurplus() - raffleType.getSubtractor())
                    .build();
        }

        //4.构建对象并返回
        return CreatePartakeOrderAggregate.builder()
                .raffleActivityAccountEntity(raffleActivityAccountEntity)
                .isExistAccountDay(isExistAccountDay)
                .raffleActivityAccountDayEntity(raffleactivityAccountDayEntity)
                .isExistAccountMonth(isExistAccountMonth)
                .raffleActivityAccountMonthEntity(raffleactivityAccountMonthEntity).build();
    }

    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, LocalDateTime now, RaffleTypeVO raffleType) {

        RaffleActivityEntity activityEntity = activityRepository.queryRaffleActivity(activityId);

        return UserRaffleOrderEntity.builder().userId(userId)
                .activityId(activityId)
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                .raffleType(raffleType)
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(now)
                .orderState(UserRaffleOrderStateVO.create)
                .build();
    }

    /**
     * 查询某个用户在某个活动的已抽奖次数
     *
     * @param userId
     * @param activityId
     * @return
     */
    @Override
    public Integer queryCompletedDrawCount(String userId, Long activityId) {
        return activityRepository.queryCompletedDrawCount(userId, activityId);
    }
}
