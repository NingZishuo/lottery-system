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
 * 库存不足
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId,Integer completedDrawCount) {
        log.info("抽奖中规则过滤-幸运奖 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, awardId, ruleModel());

        if (strategyRuleEntity != null) {
            log.info("幸运奖配置:{}",strategyRuleEntity.getRuleValue());
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultTreeFactory
                            .StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue(strategyRuleEntity.getRuleValue())
                            .build())
                    .build();
        }

        //如果没有在strategy_rule中配置幸运奖 默认发1-10积分
        log.info("未配置幸运奖,默认配置:1,10");
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory
                        .StrategyAwardVO.builder()
                        .awardId(awardId)
                        .awardRuleValue("1,10")
                        .build())
                .build();


    }


    @Override
    public String ruleModel() {
        return DefaultTreeFactory.LogicModel.RULE_LUCK_AWARD.getRuleModel();
    }
}
