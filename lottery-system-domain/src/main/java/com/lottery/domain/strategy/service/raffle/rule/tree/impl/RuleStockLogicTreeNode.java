package com.lottery.domain.strategy.service.raffle.rule.tree.impl;

import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.tree.ILogicTreeNode;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 库存不足(其实和luckAwardLogic是可以合并逻辑的)
 */
@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {

    @Autowired
    private IStrategyDispatch strategyDispatch;

    @Autowired
    private IStrategyRepository strategyRepository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Long awardId,Integer completedDrawCount) {
        log.info("抽奖中规则过滤-库存扣减 userId={}, strategyId={}, awardId={}", userId, strategyId, awardId);
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, awardId, ruleModel());
        //0.注意 幸运奖机制必须存在才能库存扣减 如果不存在 默认库存无限
        if (strategyRuleEntity != null) {
            //0.获取策略对应活动的结束时间
            LocalDateTime activityEndTime = strategyRepository.queryActivityEndTimeByStrategyId(strategyId);
            //1.扣减库存
            Boolean status = strategyRepository.subtractionAwardStock(strategyId, awardId,activityEndTime);
            //2.扣减库存失败,接管发送幸运奖
            if (!status) {
                //3.库存<0 发放幸运奖
                log.info("抽奖中规则过滤-库存不足接管");
                return DefaultTreeFactory.TreeActionEntity.builder()
                        .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                        .build();

            }
        }
        //4.无限库存或者库存足够 则放行
        //写入消息队列,延迟消费更新数据库记录
        log.info("抽奖中规则过滤-库存充足或未配置库存,放行");
        strategyRepository.awardStockConsumeSendQueue(StrategyAwardStockKeyVO.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .build());
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .build())
                .build();

    }


    @Override
    public String ruleModel() {
        return DefaultTreeFactory.LogicModel.RULE_LUCK_AWARD.getRuleModel();
    }
}
