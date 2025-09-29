package com.lottery.domain.strategy.service.raffle.rule.tree.impl;

import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.raffle.rule.tree.ILogicTreeNode;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 用户抽奖n次后解锁对应奖品逻辑
 */
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Autowired
    private IStrategyRepository strategyRepository;


    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId,Integer completedDrawCount) {
        log.info("抽奖中规则过滤-lock锁机制 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, awardId, ruleModel());
        //1.检测是否存在该机制
        if (strategyRuleEntity != null) {
            //2.对比已经抽奖次数和目标解锁次数
            if (completedDrawCount <= Integer.parseInt(strategyRuleEntity.getRuleValue())) {
                //3.如果用户抽奖次数不达标,接管
                log.info("抽奖中规则过滤,用户抽奖"+completedDrawCount+"次,不满足"+strategyRuleEntity.getRuleValue()+"次抽奖条件");
                return DefaultTreeFactory.TreeActionEntity.builder()
                        .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                        .build();
            }

        }
        log.info("抽奖中规则过滤-lock锁放行");
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }

    @Override
    public String ruleModel() {
        return DefaultTreeFactory.LogicModel.RULE_LOCK.getRuleModel();
    }
}
