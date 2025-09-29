package com.lottery.domain.strategy.service.armory;

/**
 * 策略抽奖的调度
 */
public interface IStrategyDispatch {

    /**
     * 得到一个随机的抽奖id
     * @return
     */
    Long getRandomAwardId(Long strategyId);

    /**
     * 得到一个随机的抽奖id 保底版
     * @param strategyId 哪个strategy
     * @param ruleWeightValue 要抽哪个保底呢
     * @return
     */
    Long getRandomAwardId(Long strategyId,int ruleWeightValue);


    /**
     * 判断是否存在某一抽数的保底
     * @param strategyId which strategy
     * @param ruleWeightValue pity value
     * @return
     */
    Boolean isRuleWeightValueExists(Long strategyId, int ruleWeightValue);

}
