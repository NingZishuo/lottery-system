package com.lottery.domain.strategy.service.raffle.rule.chain.impl;

import com.lottery.domain.strategy.model.entity.StrategyBlackListEntity;
import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.raffle.rule.chain.AbstractLoginChain;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 黑名单过滤逻辑
 */
@Slf4j
@Component("rule_black_list")
public class RuleBlackListLogicChain extends AbstractLoginChain {

    @Autowired
    private IStrategyRepository strategyRepository;

    /**
     * 责任链接口
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultLogicChainFactory.ChainActionEntity logic(String userId, Long strategyId,Integer completedDrawCount) {
        log.info("抽奖前规则过滤-黑名单开始 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel());
        //1.查询该strategy_rule是否有黑名单机制
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, ruleModel());
        if (strategyRuleEntity != null) {
            //2.查询用户是否在该策略的黑名单之下
            StrategyBlackListEntity strategyBlackListedUserEntity = strategyRepository.queryStrategyBlackListedUser(strategyId, userId);
            //3.假如在黑名单中
            if (strategyBlackListedUserEntity != null) {
                log.info("抽奖前规则过滤-黑名单接管,awardId:{}", strategyBlackListedUserEntity.getAwardId());
                //4.返回奖品ID就完事了
                return DefaultLogicChainFactory.ChainActionEntity.builder()
                        .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                        .strategyAwardVO(DefaultLogicChainFactory.StrategyAwardVO.builder()
                                .awardId(strategyBlackListedUserEntity.getAwardId())
                                .ruleModel(ruleModel())
                                .awardRuleValue(strategyRuleEntity.getRuleValue())
                                .build())
                        .build();
            }
        }
        log.info("规则过滤-黑名单放行");
        return next().logic(userId, strategyId,completedDrawCount);
    }

    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.RULE_BLACK_LIST.getRuleModel();
    }
}
