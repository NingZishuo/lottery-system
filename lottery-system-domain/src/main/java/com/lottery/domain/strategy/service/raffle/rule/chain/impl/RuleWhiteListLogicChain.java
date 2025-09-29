package com.lottery.domain.strategy.service.raffle.rule.chain.impl;

import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.model.entity.StrategyWhiteListEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.chain.AbstractLoginChain;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 白名单过滤逻辑
 */
@Slf4j
@Component("rule_white_list")
public class RuleWhiteListLogicChain extends AbstractLoginChain {

    @Autowired
    private IStrategyRepository strategyRepository;

    @Autowired
    private IStrategyDispatch strategyDispatch;


    /**
     * 责任链接口
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultLogicChainFactory.ChainActionEntity logic(String userId, Long strategyId,Integer completedDrawCount) {
        log.info("抽奖前规则过滤-白名单开始 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel());
        //1.查询该strategy_rule是否有白名单机制(非必要)
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, ruleModel());
        if (strategyRuleEntity != null) {
            //2.查询用户是否在该策略的白名单之下
            StrategyWhiteListEntity strategyWhiteListedUserEntity = strategyRepository.queryStrategyWhiteListedUser(strategyId, userId);
            //3.假如在白名单中
            if (strategyWhiteListedUserEntity != null) {
                //4.获取白名单用户的最低奖励门槛
                Integer lowestRuleWeight = strategyWhiteListedUserEntity.getRuleWeight();
                //5.获取用户已抽奖次数
                completedDrawCount = strategyRepository.addCompletedDrawCount(userId,strategyId);
                //6.用户无法达到保底,强制使用最低奖励门槛,此时需要接管
                if (completedDrawCount <= lowestRuleWeight) {
                    Long awardId = strategyDispatch.getRandomAwardId(strategyId, lowestRuleWeight);
                    log.info("抽奖前规则过滤-白名单接管 lowestRuleWeight:{} awardId:{}",lowestRuleWeight ,awardId);
                    return DefaultLogicChainFactory.ChainActionEntity.builder()
                            .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                            .strategyAwardVO(DefaultLogicChainFactory.StrategyAwardVO.builder()
                                    .awardId(awardId)
                                    .ruleModel(ruleModel())
                                    .completedDrawCount(completedDrawCount)
                                    .build())
                            .build();
                }
            }
        }
        //6.两种情况:非白名单用户,直接放行
        //          白名单用户,但是达到了更高的保底奖励门槛
        log.info("抽奖前规则过滤-白名单放行");
        return next().logic(userId, strategyId,completedDrawCount);
    }

    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.RULE_WHITE_LIST.getRuleModel();
    }
}
