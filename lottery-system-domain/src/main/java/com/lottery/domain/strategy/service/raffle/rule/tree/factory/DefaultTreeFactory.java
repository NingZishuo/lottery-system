package com.lottery.domain.strategy.service.raffle.rule.tree.factory;




import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.model.valobj.RuleTreeVO;
import com.lottery.domain.strategy.service.raffle.rule.tree.ILogicTreeNode;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine.IDecisionTreeEngine;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 默认规则树规则工厂
 */
@Service
public class DefaultTreeFactory {
    private final Map<String, ILogicTreeNode> logicFilterMap;


    @Autowired
    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicFilterMap) {
        this.logicFilterMap = logicFilterMap;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicFilterMap, ruleTreeVO);
    }



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity{

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
    public static class StrategyAwardVO{
        /**
         * 抽奖奖品ID
         */
        private Long awardId;
        /**
         * 因什么而被接管(未用上)
         */
        private String ruleModel;
        /**
         * 接管后所替换的规则
         * 当然你也可以不设置这个属性
         * 然后用awardId ruleModel查询出来也行
         */
        private String awardRuleValue;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {
        //抽奖中
        RULE_LOCK("rule_lock","【抽奖中规则】抽奖n次后解锁"),
        RULE_LUCK_AWARD("rule_luck_award","【抽奖中规则】幸运奖(未解锁或库存见底)"),
        ;
        private final String ruleModel;
        private final String info;

    }



}
