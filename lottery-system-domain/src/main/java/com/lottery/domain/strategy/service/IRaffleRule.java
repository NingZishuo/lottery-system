package com.lottery.domain.strategy.service;

import com.lottery.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * 抽奖规则接口
 */
public interface IRaffleRule {

    /**
     * 计算awardId对应的上锁次数
     * @param awardIdList
     * @return
     */
    Map<Long, Integer> queryAwardRuleLockCount(Long strategyId,List<Long> awardIdList);

    /**
     * 获取权重及其抽奖奖品的范围
     * @param activityId
     * @return
     */
    Map<String, List<StrategyAwardEntity>> queryRaffleStrategyRuleWeightAndAwards(Long activityId);
}
