package com.lottery.domain.strategy.service.raffle.rule.chain.impl;

import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.chain.AbstractLoginChain;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 权重过滤逻辑(保底)
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLoginChain {

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
        log.info("抽奖前规则过滤-权重开始 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel());

        //1.确认是否存在保底机制
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, ruleModel());
        //2.不存在的话直接放行
        if (strategyRuleEntity == null) {
            log.info("抽奖前规则过滤-权重放行");
            return next().logic(userId, strategyId,completedDrawCount);
        }

        Map<String, List<Long>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        //2.格式不对 默认无法触发保底 直接放行
        if (ruleWeightValues == null || ruleWeightValues.isEmpty()) {
            //throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_FORMAT_IS_WRONG.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_FORMAT_IS_WRONG.getInfo());
            //假如4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
            log.info("抽奖前规则过滤-权重放行");
            return next().logic(userId, strategyId,completedDrawCount);
        }
        //3.获取用户的保底门槛 比如用户积分5500 则获取5000的积分保底门槛
        //注意 这里又改成用户已抽奖次数了 其实差不多
        if (completedDrawCount == null) {
            completedDrawCount = strategyRepository.addCompletedDrawCount(userId, strategyId);
        }
        //4.判断该抽数是否存在保底
        if (strategyDispatch.isRuleWeightValueExists(strategyId,completedDrawCount)) {
            //5.达到某个保底门槛
            Long awardId = strategyDispatch.getRandomAwardId(strategyId, completedDrawCount);
            log.info("抽奖前规则过滤-权重接管 ruleWeightValue:{} awardId:{}", completedDrawCount, awardId);
            return DefaultLogicChainFactory.ChainActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultLogicChainFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .ruleModel(ruleModel())
                            .build())
                    .build();
        }
        //5.未触及任何保底 放行
        log.info("抽奖前规则过滤-权重放行");
        return next().logic(userId, strategyId,completedDrawCount);
    }

    @Override
    protected String ruleModel() {
        return DefaultLogicChainFactory.LogicModel.RULE_WEIGHT.getRuleModel();
    }
}
