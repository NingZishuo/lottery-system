package com.lottery.domain.strategy.repository;

import com.lottery.domain.strategy.model.entity.*;
import com.lottery.domain.strategy.model.valobj.RuleTreeVO;
import com.lottery.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略仓储接口
 */
public interface IStrategyRepository {


    /**
     * 查询策略配置
     * @param strategyId
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardEntityList(Long strategyId);



    /**
     * 存储到redis中
     * @param key
     * @param sumRateRange
     * @param shuffleStrategyAwardSearchRateTable
     */
    void storeStrategyAwardSearchRateTable(String key, Integer sumRateRange, HashMap<Integer, Long> shuffleStrategyAwardSearchRateTable);


    /**
     * 得到总的需要生成随机数的范围
     *
     * @param key
     * @return
     */
    int getSumRateRange(String key);

    /**
     * 拿取奖品的id
     * @param key
     * @param rateKey
     * @return
     */
    Long getStrategyAwardAssemble(String key, int rateKey);


    /**
     * 根据id查询Strategy
     * @param strategyId
     * @return
     */
    StrategyEntity queryStrategy(Long strategyId);

    /**
     * 根据 strategyId,ruleWeight 查询 StrategyRuleEntity
     * @param strategyId strategyId
     * @param ruleModel ruleModel
     * @return
     */
    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    /**
     * 根据 strategyId,awardId,ruleWeight 查询 StrategyRuleEntity
     * @param strategyId
     * @param awardId
     * @param ruleModel
     * @return
     */
    StrategyRuleEntity queryStrategyRule(Long strategyId, Long awardId, String ruleModel);


    /**
     * 查询黑名单用户及其固定奖励
     * @param strategyId
     * @param userId
     * @return
     */
    StrategyBlackListEntity queryStrategyBlackListedUser(Long strategyId,String userId);

    /**
     * 查询白名单用户及其最低奖励门槛
     * @param strategyId
     * @param userId
     * @return
     */
    StrategyWhiteListEntity queryStrategyWhiteListedUser(Long strategyId,String userId);

    /**
     * 根据awardId 查询奖品实体
     * @param awardId
     * @return
     */
    RaffleAwardEntity queryAwardEntity(Long awardId);

    /**
     * 根据strategyId awardId策略奖品实体
     * @param strategyId
     * @param awardId
     * @return
     */
    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Long awardId);


    /**
     * 根据strategyId查询策略树值对象 同时装载节点及其连线
     * @param strategyId
     * @return
     */
    RuleTreeVO queryRuleTreeVO(Long strategyId);


    /**
     * 缓存策略奖励的库存到redis中
     * @param strategyId
     * @param awardId
     * @param awardCount
     */
    void cacheStrategyAwardCount(Long strategyId, Long awardId,Long awardCount);

    /**
     * 扣减库存
     * @param strategyId
     * @param awardId
     * @return
     */
    Boolean subtractionAwardStock(Long strategyId, Long awardId);

    /**
     * 扣减库存 带活动结束时间
     * @param strategyId
     * @param awardId
     * @param activityEndTime
     * @return
     */
    Boolean subtractionAwardStock(Long strategyId, Long awardId, LocalDateTime activityEndTime);


    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    StrategyAwardStockKeyVO takeQueueValue();


    void updateStrategyAwardStock(Long strategyId, Long awardId);


    Long queryStrategyIdByActivityId(Long activityId);

    Long queryActivityIdByStrategyId(Long strategyId);

    /**
     * 查询用户在某个活动的已经抽奖次数
     * @param userId
     * @param strategyId
     * @return
     */
    Integer queryCompletedDrawCount(String userId, Long strategyId);

    /**
     * 查询用户在某个活动的已经抽奖次数+1
     * @param userId
     * @param strategyId
     * @return
     */
    Integer addCompletedDrawCount(String userId, Long strategyId);


    /**
     *  计算awardId对应的上锁次数
     * @param awardIdList
     * @return
     */
    Map<Long, Integer> queryAwardRuleLockCount(Long strategyId,List<Long> awardIdList);

    /**
     * 根据strategyId查询对应活动结束时间
     * @param strategyId
     * @return
     */
    LocalDateTime queryActivityEndTimeByStrategyId(Long strategyId);

    /**
     * 根据awardIds获取StrategyAwardEntity
     * @param awardIds
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardEntityByAwardIds(Long strategyId,List<Long> awardIds);

    /**
     * 某一保底是否真的存在
     * @param key
     * @return
     */
    Boolean isRuleWeightValueExists(String key);
}
