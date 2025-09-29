package com.lottery.domain.award.repository;

import com.lottery.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.lottery.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.lottery.domain.award.model.entity.AwardEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;

import java.util.List;

/**
 * 中奖(发奖)相关仓储接口
 */
public interface IAwardRepository {


    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    void saveUserAwardRecordList(List<UserAwardRecordAggregate> userAwardRecordAggregateList);


    AwardEntity queryAward(Long awardId);


    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);


    List<UserAwardRecordEntity> queryUserAwardRecordList(String userId, Long activityId);
}
