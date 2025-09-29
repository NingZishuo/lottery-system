package com.lottery.domain.strategy.service.armory;

/**
 * 策略装配库(兵工厂)、负责初始化策略计算
 */
public interface IStrategyArmory {

   /**
    * 根据strategyId装配对应抽奖策略
    * @param strategyId
    */
   boolean assembleLotteryStrategy(Long strategyId);

   /**
    * 根据活动ID装配
    */
   boolean assembleLotteryStrategyByActivityId(Long activityId);


}
