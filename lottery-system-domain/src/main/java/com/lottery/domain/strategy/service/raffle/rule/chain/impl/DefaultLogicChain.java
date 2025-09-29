package com.lottery.domain.strategy.service.raffle.rule.chain.impl;

import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.chain.AbstractLoginChain;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 兜底责任链
 */
@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLoginChain {

    @Autowired
    private IStrategyDispatch strategyDispatch;
    /**
     * 兜底责任链 直接进行抽奖即可
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultLogicChainFactory.ChainActionEntity logic(String userId, Long strategyId,Integer completedDrawCount) {
        Long awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userID:{}, strategyId:{} awardId:{}", userId, strategyId, awardId);
        return DefaultLogicChainFactory.ChainActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .strategyAwardVO(DefaultLogicChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .completedDrawCount(completedDrawCount)
                        .build())
                .build();
    }

    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.DEFAULT.getRuleModel();
    }
}
