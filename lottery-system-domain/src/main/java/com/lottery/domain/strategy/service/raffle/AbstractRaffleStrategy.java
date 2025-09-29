package com.lottery.domain.strategy.service.raffle;

import com.lottery.domain.strategy.model.entity.*;
import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.domain.strategy.service.IRaffleStrategy;
import com.lottery.domain.strategy.service.armory.IStrategyDispatch;
import com.lottery.domain.strategy.service.raffle.rule.chain.factory.DefaultLogicChainFactory;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽奖策略抽象类
 * 为什么要做成抽象类?
 * 1.分割出抽奖业务和抽奖前、中、后过滤规则
 * 1.1 只需要更改过滤规则:写新的DefaultRaffleStrategy继承AbstractRaffleStrategy
 * 1.2 需要更改抽奖业务(隐含更改规则): 接口和该抽象类写新的函数
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    //策略仓储 -> 负责从数据库或者redis中读取数据,不作任何交互
    protected IStrategyRepository strategyRepository;

    //策略调配 -> Armory负责装配 Dispatch负责调用抽奖接口之类的
    protected IStrategyDispatch strategyDispatch;

    private final DefaultLogicChainFactory defaultLogicChainFactory;

    private final DefaultTreeFactory defaultTreeFactory;


    public AbstractRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultLogicChainFactory defaultLogicChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.strategyRepository = strategyRepository;
        this.strategyDispatch = strategyDispatch;
        this.defaultLogicChainFactory = defaultLogicChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    /**
     * 执行抽奖,返回奖品实体
     * 抽奖要执行很多逻辑,并非在test中那样调接口抽个奖返回“奖品id:102”就完事了
     *
     * @param raffleFactorEntity 哪个用户 用哪个抽奖策略
     * @return 奖品实体
     */
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();

        if (userId == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //2.开启责任链处理(openLogicChain()方法有strategyId是否为null)
        DefaultLogicChainFactory.ChainActionEntity chainActionEntity = this.raffleLogicChain(userId, strategyId,null);
        DefaultLogicChainFactory.StrategyAwardVO raffleBeforeStrategyAwardVO = chainActionEntity.getStrategyAwardVO();
        //3.初始奖品ID,如果是黑名单和保底,直接返回该奖品
        Long awardId = null;
        if (chainActionEntity.getRuleLogicCheckType() == RuleLogicCheckTypeVO.TAKE_OVER) {
            //注意,白名单虽然圈定中奖范围,但是还得过抽奖中过滤,即是否锁定,是否有货
            //所以只有黑名单和保底可以直接返回那个奖项
            awardId = raffleBeforeStrategyAwardVO.getAwardId();
            if (!raffleBeforeStrategyAwardVO.getRuleModel().equals(DefaultLogicChainFactory.LogicModel.RULE_WHITE_LIST.getRuleModel())){
                return buildRaffleAwardEntity(strategyId,awardId,raffleBeforeStrategyAwardVO.getAwardRuleValue());
            }
        }
        //4.放行 直接进行普通抽奖即可
        if (awardId ==null){
            awardId = strategyDispatch.getRandomAwardId(strategyId);
        }
        //4.抽奖中过滤
        DefaultTreeFactory.TreeActionEntity treeActionEntity = this.raffleLogicTree(userId, strategyId, awardId,raffleBeforeStrategyAwardVO.getCompletedDrawCount());
        //5.接管后,要按配置发奖,而不是之前传过来的奖品(awardId作废)
        if (treeActionEntity != null) {
            if (treeActionEntity.getRuleLogicCheckType() == RuleLogicCheckTypeVO.TAKE_OVER) {
                String awardRuleValue = treeActionEntity.getStrategyAwardVO().getAwardRuleValue();
                return buildRaffleAwardEntity(strategyId,awardId,awardRuleValue);
            }
        }

        //通关所有过滤,发奖(其实这里只有普通抽奖和白名单的awardId)
        return buildRaffleAwardEntity(strategyId,awardId,null);
    }

    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId , Long awardId,String awardRuleValue) {

        StrategyAwardEntity strategyAwardEntity = strategyRepository.queryStrategyAwardEntity(strategyId, awardId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .awardTitle(strategyAwardEntity.getAwardTitle())
                .awardConfig(awardRuleValue)
                .sort(strategyAwardEntity.getSort())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    public abstract DefaultLogicChainFactory.ChainActionEntity raffleLogicChain(String userId, Long strategyId,Integer completedDrawCount);

    /**
     * 抽奖结果过滤，决策树抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.TreeActionEntity raffleLogicTree(String userId, Long strategyId, Long awardId,Integer completedDrawCount);

}
