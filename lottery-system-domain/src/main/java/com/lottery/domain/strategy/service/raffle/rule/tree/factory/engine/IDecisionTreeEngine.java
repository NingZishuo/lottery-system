package com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine;


import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;

/**
 * 执行引擎 规则树的组合接口
 */
public interface IDecisionTreeEngine {

    DefaultTreeFactory.TreeActionEntity process(String userId, Long strategyId, Long awardId,Integer completedDrawCount);

}
