package com.lottery.domain.strategy.service.raffle.rule.tree;

import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId,Integer completedDrawCount);

    String ruleModel();

}
