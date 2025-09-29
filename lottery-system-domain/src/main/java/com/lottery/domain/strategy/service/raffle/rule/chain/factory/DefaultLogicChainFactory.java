package com.lottery.domain.strategy.service.raffle.rule.chain.factory;


import com.lottery.domain.strategy.model.entity.StrategyEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.raffle.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 默认责任链规则工厂
 */
@Service
public class DefaultLogicChainFactory {
    private final Map<String, ILogicChain> logicFilterMap;

    private final IStrategyRepository strategyRepository;

    @Autowired
    public DefaultLogicChainFactory(Map<String, ILogicChain> logicFilterMap, IStrategyRepository strategyRepository) {
        this.logicFilterMap = logicFilterMap;
        this.strategyRepository = strategyRepository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategyEntity = strategyRepository.queryStrategy(strategyId);
        String[] ruleModels = strategyEntity.ruleModels();
        if (ruleModels == null || ruleModels.length == 0) {
            return logicFilterMap.get("default");
        }

        ILogicChain iFirstLogicChain = logicFilterMap.get(ruleModels[0]);
        ILogicChain current = iFirstLogicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            //注意这里返回的是下一个节点
            current = current.appendNext(logicFilterMap.get(ruleModels[i]));
        }
        current.appendNext(logicFilterMap.get("default"));

        return iFirstLogicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChainActionEntity{

        private RuleLogicCheckTypeVO ruleLogicCheckType;;

        /**
         * 如果发奖过程有意外 则用这个装载异常奖励(比如兜底奖)
         * 如果无意外那就是ALLOW 这里也肯定为null了
         */
        private StrategyAwardVO strategyAwardVO;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /**
         * 奖品ID
         */
        private Long awardId;

        /**
         * 因什么而被接管
         */
        private String ruleModel;
        /**
         * 接管后所替换的规则
         */
        private String awardRuleValue;
        /**
         * 用户已抽奖次数
         */
        private Integer completedDrawCount;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {
        //抽奖前
        DEFAULT("default", "未触发规则，正常抽奖"),
        RULE_WEIGHT("rule_weight", "【抽奖前规则】保底机制pityLimit"),
        RULE_BLACK_LIST("rule_black_list", "【抽奖前规则】黑名单,直接返回"),
        RULE_WHITE_LIST("rule_white_list", "【抽奖前规则】白名单,享受最低奖励门槛"),
        ;

        private final String ruleModel;
        private final String info;
    }


}
