package com.lottery.domain.strategy.service.raffle;

import com.lottery.domain.strategy.model.entity.*;
import com.lottery.domain.strategy.model.valobj.RuleTreeVO;
import com.lottery.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.IRaffleAward;
import com.lottery.domain.strategy.service.IRaffleRule;
import com.lottery.domain.strategy.service.IRaffleStock;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.chain.ILogicChain;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine.IDecisionTreeEngine;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleStock, IRaffleAward, IRaffleRule {
    @Autowired
    private DefaultTreeFactory defaultTreeFactory;

    @Autowired
    private DefaultLogicChainFactory defaultLogicChainFactory;

    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(strategyRepository, strategyDispatch, defaultLogicChainFactory, defaultTreeFactory);
    }


    /**
     * 抽奖计算，责任链抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultLogicChainFactory.ChainActionEntity raffleLogicChain(String userId, Long strategyId, Integer completedDrawCount) {
        ILogicChain iLogicChain = defaultLogicChainFactory.openLogicChain(strategyId);
        return iLogicChain.logic(userId, strategyId, completedDrawCount);
    }

    /**
     * 抽奖结果过滤，决策树抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
     */
    @Override
    public DefaultTreeFactory.TreeActionEntity raffleLogicTree(String userId, Long strategyId, Long awardId, Integer completedDrawCount) {

        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVO(strategyId);

        IDecisionTreeEngine iDecisionTreeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);


        return iDecisionTreeEngine.process(userId, strategyId, awardId, completedDrawCount);
    }

    /**
     * 获取奖品库存消耗队列
     *
     * @return 奖品库存Key信息
     * @throws InterruptedException 异常
     */
    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return strategyRepository.takeQueueValue();
    }

    /**
     * 更新奖品库存消耗记录
     *
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     */
    @Override
    public void updateStrategyAwardStock(Long strategyId, Long awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }

    /**
     * 查询策略奖品
     *
     * @param strategyId
     * @return
     */
    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return strategyRepository.queryStrategyAwardEntityList(strategyId);
    }

    /**
     * 根据activityId查询策略奖品
     *
     * @param activityId
     * @return
     */
    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        return this.queryRaffleStrategyAwardList(strategyId);
    }

    /**
     * 计算awardId对应的上锁次数
     *
     * @param awardIdList
     * @return
     */
    @Override
    public Map<Long, Integer> queryAwardRuleLockCount(Long strategyId, List<Long> awardIdList) {
        return strategyRepository.queryAwardRuleLockCount(strategyId, awardIdList);
    }


    /**
     * 获取权重及其抽奖奖品的范围
     *
     * @param activityId
     * @return
     */
    @Override
    public Map<String, List<StrategyAwardEntity>> queryRaffleStrategyRuleWeightAndAwards(Long activityId) {
        //0.获取策略ID
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        //1.查询该策略的保底权重配置
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, DefaultLogicChainFactory.LogicModel.RULE_WEIGHT.getRuleModel());
        if (strategyRuleEntity == null) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        //2.40:102,103,104,105 50:102,103,104,105,106,107 60:102,103,104,105,106,107,108,109
        Map<String, List<Long>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();

        Map<String, List<StrategyAwardEntity>> map = new LinkedHashMap<>();
        for (String ruleWeight : ruleWeightValues.keySet()) {
            //3.获取102,103,104,105更加详细的信息
            List<StrategyAwardEntity> strategyAwardEntityList = strategyRepository.queryStrategyAwardEntityByAwardIds(strategyId, ruleWeightValues.get(ruleWeight));
            map.put(ruleWeight, strategyAwardEntityList);
        }
        return map;
    }
}
