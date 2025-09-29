package com.lottery.domain.strategy.service;


import com.lottery.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品查询接口
 */
public interface IRaffleAward {

    /**
     * 查询策略奖品
     * @param strategyId
     * @return
     */
   List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);


    /**
     * 根据activityId查询策略奖品
     * @param activityId
     * @return
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId);

}
