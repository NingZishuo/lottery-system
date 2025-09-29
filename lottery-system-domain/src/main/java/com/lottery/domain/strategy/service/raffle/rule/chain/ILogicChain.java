package com.lottery.domain.strategy.service.raffle.rule.chain;

import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;

/**
 * 责任链接口
 */
public interface ILogicChain extends ILogicChainArmory{
    /**
     * 责任链接口
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    DefaultLogicChainFactory.ChainActionEntity logic(String userId, Long strategyId,Integer completedDrawCount);


}
