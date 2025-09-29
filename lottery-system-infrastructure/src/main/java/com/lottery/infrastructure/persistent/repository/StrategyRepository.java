package com.lottery.infrastructure.persistent.repository;

import com.lottery.domain.strategy.model.entity.*;
import com.lottery.domain.strategy.model.valobj.*;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.infrastructure.persistent.dao.*;
import com.lottery.infrastructure.persistent.po.*;
import com.lottery.infrastructure.persistent.redis.IRedisService;
import com.lottery.types.common.Constants;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 策略仓储实现
 */
@Service
@Slf4j
public class StrategyRepository implements IStrategyRepository {

    @Autowired
    private IAwardDao awardDao;

    @Autowired
    private IStrategyAwardDao strategyAwardDao;

    @Autowired
    private IStrategyDao strategyDao;

    @Autowired
    private IStrategyRuleDao strategyRuleDao;

    @Autowired
    private IRedisService redisService;

    @Autowired
    private IStrategyBlackListDao strategyBlackListDao;

    @Autowired
    private IStrategyWhiteListDao strategyWhiteListDao;

    @Autowired
    private IRuleTreeDao ruleTreeDao;

    @Autowired
    private IRuleTreeNodeDao ruleTreeNodeDao;

    @Autowired
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Autowired
    private IRaffleActivityDao raffleActivityDao;

    @Autowired
    private IRaffleActivityAccountDao raffleActivityAccountDao;


    /**
     * 查询策略配置
     *
     * @param strategyId
     * @return
     */
    @Override
    public List<StrategyAwardEntity> queryStrategyAwardEntityList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        //尝试从redis中读取
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) {
            return strategyAwardEntities;
        }
        strategyAwardEntities = new ArrayList<>();
        //从数据库中读取
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        if (strategyAwards != null && !strategyAwards.isEmpty()) {
            for (StrategyAward strategyAward : strategyAwards) {
                StrategyAwardEntity strategyAwardEntity = new StrategyAwardEntity();
                BeanUtils.copyProperties(strategyAward, strategyAwardEntity);
                strategyAwardEntities.add(strategyAwardEntity);
            }
            //加入缓存
            redisService.setValue(cacheKey, strategyAwardEntities);
        }
        return strategyAwardEntities;
    }


    @Override
    public void storeStrategyAwardSearchRateTable(String key, Integer sumRateRange, HashMap<Integer, Long> shuffleStrategyAwardSearchRateTable) {
        //1.存储用于strategyId的所要生成随机数的范围sumRateRange
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, sumRateRange.intValue());
        //2.存储概率查找表map
        RMap<Integer, Long> cacheAwardRateMap = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheAwardRateMap.putAll(shuffleStrategyAwardSearchRateTable);
    }


    /**
     * 得到总的需要生成随机数的范围
     *
     * @param key
     * @return
     */
    @Override
    public int getSumRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    /**
     * 拿取奖品的id
     *
     * @param key
     * @param rateKey
     * @return
     */
    @Override
    public Long getStrategyAwardAssemble(String key, int rateKey) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key;
        if (!redisService.isExists(cacheKey)) {
            throw new AppException(ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getFromMap(cacheKey, rateKey);
    }

    /**
     * 某一保底是否真的存在
     *
     * @param key
     * @return
     */
    @Override
    public Boolean isRuleWeightValueExists(String key) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key;
        return redisService.isExists(cacheKey);
    }

    /**
     * 根据id查询Strategy
     *
     * @param strategyId
     * @return
     */
    @Override
    public StrategyEntity queryStrategy(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (strategyEntity != null) {
            return strategyEntity;
        }
        strategyEntity = new StrategyEntity();
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        if (strategy == null) {
            throw new AppException(ResponseCode.STRATEGY_IS_NULL.getCode(), ResponseCode.STRATEGY_IS_NULL.getInfo());
        }
        BeanUtils.copyProperties(strategy, strategyEntity);
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }

    /**
     * 根据 strategyId,ruleWeight 查询 StrategyRuleEntity
     * 未做缓存
     *
     * @param strategyId strategyId
     * @param ruleModel  ruleWeight
     * @return
     */
    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule strategyRule = StrategyRule.builder()
                .strategyId(strategyId)
                .ruleModel(ruleModel)
                .build();
        strategyRule = strategyRuleDao.queryStrategyRule(strategyRule);
        if (strategyRule == null) {
            return null;
        }
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        BeanUtils.copyProperties(strategyRule, strategyRuleEntity);
        return strategyRuleEntity;
    }

    /**
     * 根据 strategyId,awardId,ruleWeight 查询 StrategyRuleEntity
     *
     * @param strategyId
     * @param awardId
     * @param ruleModel
     * @return
     */
    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, Long awardId, String ruleModel) {
        StrategyRule strategyRule = StrategyRule.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .ruleModel(ruleModel)
                .build();
        strategyRule = strategyRuleDao.queryStrategyRule(strategyRule);
        if (strategyRule == null) {
            return null;
        }
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        BeanUtils.copyProperties(strategyRule, strategyRuleEntity);
        return strategyRuleEntity;
    }

    /**
     * 查询黑名单用户及其固定奖励
     *
     * @param strategyId
     * @param userId
     * @return
     */
    @Override
    public StrategyBlackListEntity queryStrategyBlackListedUser(Long strategyId, String userId) {
        StrategyBlackList strategyBlackList = StrategyBlackList.builder()
                .strategyId(strategyId)
                .userId(userId)
                .build();
        StrategyBlackList blackListedUser = strategyBlackListDao.queryStrategyBlackList(strategyBlackList);
        if (blackListedUser == null) {
            return null;
        }
        StrategyBlackListEntity strategyBlackListEntity = new StrategyBlackListEntity();
        BeanUtils.copyProperties(blackListedUser, strategyBlackListEntity);
        return strategyBlackListEntity;
    }

    /**
     * 查询白名单用户及其最低奖励门槛
     *
     * @param strategyId
     * @param userId
     * @return
     */
    @Override
    public StrategyWhiteListEntity queryStrategyWhiteListedUser(Long strategyId, String userId) {
        StrategyWhiteList strategyWhiteList = StrategyWhiteList.builder()
                .strategyId(strategyId)
                .userId(userId)
                .build();
        StrategyWhiteList whiteListedUser = strategyWhiteListDao.queryStrategyWhiteList(strategyWhiteList);
        if (whiteListedUser == null) {
            return null;
        }
        StrategyWhiteListEntity strategyWhiteListEntity = new StrategyWhiteListEntity();
        BeanUtils.copyProperties(whiteListedUser, strategyWhiteListEntity);
        return strategyWhiteListEntity;
    }

    /**
     * 根据awardId 查询奖品实体
     *
     * @param awardId
     * @return
     */
    @Override
    public RaffleAwardEntity queryAwardEntity(Long awardId) {
        Award award = Award
                .builder()
                .awardId(awardId)
                .build();
        award = awardDao.queryAward(award);
        if (award == null) {
            throw new AppException(ResponseCode.AWARD_IS_NULL.getCode(), ResponseCode.AWARD_IS_NULL.getInfo());
        }
        RaffleAwardEntity raffleAwardEntity = new RaffleAwardEntity();
        BeanUtils.copyProperties(award, raffleAwardEntity);
        return raffleAwardEntity;
    }

    /**
     * 根据strategyId awardId查询策略奖品实体
     *
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Long awardId) {

        String key = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + ":" + awardId;

        StrategyAwardEntity strategyAwardEntity = redisService.getValue(key);
        if (strategyAwardEntity != null) {
            return strategyAwardEntity;
        }

        StrategyAward strategyAward = StrategyAward.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .build();
        strategyAward = strategyAwardDao.queryStrategyAward(strategyAward);
        if (strategyAward == null) {
            return null;
        }
        strategyAwardEntity = new StrategyAwardEntity();
        BeanUtils.copyProperties(strategyAward, strategyAwardEntity);
        redisService.setValue(key, strategyAwardEntity);
        return strategyAwardEntity;
    }

    /**
     * 根据treeId查询策略树值对象 同时装载节点及其连线
     *
     * @param strategyId
     * @return
     */
    @Override
    public RuleTreeVO queryRuleTreeVO(Long strategyId) {

        RuleTreeVO ruleTreeVO = redisService.getValue(Constants.RedisKey.RULE_TREE_VO_KEY + strategyId);
        if (ruleTreeVO != null) {
            return ruleTreeVO;
        }
        //1.规则树配置
        RuleTree ruleTree = ruleTreeDao.queryRuleTree(RuleTree
                .builder()
                .strategyId(strategyId)
                .build());
        if (ruleTree == null) {
            return null;
        }
        ruleTreeVO = new RuleTreeVO();
        BeanUtils.copyProperties(ruleTree, ruleTreeVO);
        String treeId = ruleTreeVO.getTreeId();

        //2.配置Map<String, RuleTreeNodeVO> treeNodeMap
        List<RuleTreeNode> ruleTreeNodeList = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);

        //3.说明不存在任何节点
        if (ruleTreeNodeList == null) {
            //不存在任何节点说明根节点也是null
            //按道理来说 假如不设置为null 那么ruleTreeVO的原始数据库的rule_lock会被拿去执行
            ruleTreeVO.setTreeRootRuleNode(null);
            return ruleTreeVO;
        }


        ruleTreeVO.setTreeNodeMap(ruleTreeNodeList.stream().map(ruleTreeNode -> {
            RuleTreeNodeVO ruleTreeNodeVO = new RuleTreeNodeVO();
            BeanUtils.copyProperties(ruleTreeNode, ruleTreeNodeVO);

            //4.获取节点特定的几个连线
            List<RuleTreeNodeLine> ruleTreeNodeLineList = ruleTreeNodeLineDao.queryRuleTreeNodeLineList(RuleTreeNodeLine.builder()
                    .treeId(treeId)
                    .ruleNodeFrom(ruleTreeNodeVO.getRuleModel())
                    .build());

            //5.连线不为空才需要设置节点setTreeNodeLineVOList
            if (ruleTreeNodeLineList != null) {
                ruleTreeNodeVO.setTreeNodeLineVOList(ruleTreeNodeLineList.stream()
                        .map(ruleTreeNodeLine -> RuleTreeNodeLineVO.builder()
                                .treeId(ruleTreeNodeLine.getTreeId())
                                .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                                .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                                .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                                .ruleLogicCheckType(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLogicCheckType()))
                                .build())
                        .collect(Collectors.toList()));
            }
            return ruleTreeNodeVO;
        }).collect(Collectors.toMap(RuleTreeNodeVO::getRuleModel, ruleTreeNodeVO -> ruleTreeNodeVO)));

        redisService.setValue(Constants.RedisKey.RULE_TREE_VO_KEY + strategyId, ruleTreeVO);

        return ruleTreeVO;
    }

    /**
     * 缓存策略奖励的库存到redis中
     *
     * @param strategyId
     * @param awardId
     */
    @Override
    public void cacheStrategyAwardCount(Long strategyId, Long awardId, Long awardCount) {
        String key = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + ":" + awardId;
        //获取缓存的库存
        Long count = redisService.getAtomicLong(key);
        if (count != null) {
            //之前装配过就不用动了
            return;
        }
        redisService.setAtomicLong(key, awardCount);
    }

    /**
     * 扣减库存
     *
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public Boolean subtractionAwardStock(Long strategyId, Long awardId) {
        //TODO 可以做个校验 万一redis取不出来对应库存呢
        String key = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + ":" + awardId;
        //TODO 注意 这里的写法明显不支持补货
        long surplus = redisService.decr(key);
        if (surplus < 0) {
            redisService.setValue(key, 0);
            return false;
        }
        //这里加锁 让每个awardId的每一个库存只能卖出去一次 间接说明这种写法是不支持补货的
        String lockKey = key + ":" + surplus;
        Boolean lock = redisService.setNx(lockKey);
        if (!lock) {
            log.info("策略奖品加锁失败:{}", lockKey);
        }
        return lock;
    }

    /**
     * 扣减库存 带活动结束时间
     *
     * @param strategyId
     * @param awardId
     * @param activityEndTime
     * @return
     */
    @Override
    public Boolean subtractionAwardStock(Long strategyId, Long awardId, LocalDateTime activityEndTime) {
        String key = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + ":" + awardId;
        long surplus = redisService.decr(key);
        if (surplus < 0) {
            redisService.setValue(key, 0);
            return false;
        }
        //这里加锁 让每个awardId的每一个库存只能卖出去一次 间接说明这种写法是不支持补货的
        String lockKey = key + ":" + surplus;
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        long expireTime = activityEndTime.atZone(zoneId).toEpochSecond() - LocalDateTime.now().atZone(zoneId).toEpochSecond() + TimeUnit.DAYS.toSeconds(1);
        Boolean lock = redisService.setNx(lockKey, expireTime, TimeUnit.SECONDS);
        if (!lock) {
            log.info("策略奖品加锁失败:{}", lockKey);
        }
        return lock;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String key = Constants.RedisKey.STRATEGY_AWARD_BLOCK_QUEUE_KEY;
        //阻塞队列:offer()有空秒插入  无空可选等待一定时间或者立即失败
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        //希望可以慢点插入 所以这里用了延迟队列而不是阻塞队列
        //即使是延迟队列 也要保证底层的阻塞队列不超容量 否则报错
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String key = Constants.RedisKey.STRATEGY_AWARD_BLOCK_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        //poll()队列不存在消息的话不会像take()一样阻塞 而是直接返回null
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Long awardId) {
        strategyAwardDao.updateStrategyAwardStock(StrategyAward.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .build());
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivity(RaffleActivity.builder()
                .activityId(activityId)
                .build());
        return raffleActivity.getStrategyId();
    }

    @Override
    public Long queryActivityIdByStrategyId(Long strategyId) {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivity(RaffleActivity.builder()
                .strategyId(strategyId)
                .build());

        return raffleActivity.getActivityId();
    }


    @Override
    public Integer queryCompletedDrawCount(String userId, Long strategyId) {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivity(RaffleActivity.builder()
                .strategyId(strategyId)
                .build());
        String cacheKey = Constants.RedisKey.ACTIVITY_USER_COMPLETED_DRAW_COUNT_KEY + raffleActivity.getActivityId() + ":" + userId;
        return redisService.getValue(cacheKey);
    }

    /**
     * 查询用户在某个活动的已经抽奖次数+1
     *
     * @param userId
     * @param strategyId
     * @return
     */
    @Override
    public Integer addCompletedDrawCount(String userId, Long strategyId) {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivity(RaffleActivity.builder()
                .strategyId(strategyId)
                .build());
        String cacheKey = Constants.RedisKey.ACTIVITY_USER_COMPLETED_DRAW_COUNT_KEY + raffleActivity.getActivityId() + ":" + userId;
        Long completedDrawCount = redisService.incr(cacheKey);
        return completedDrawCount.intValue();
    }

    /**
     * 计算awardId对应的上锁次数
     *
     * @param awardIdList
     * @return
     */
    @Override
    public Map<Long, Integer> queryAwardRuleLockCount(Long strategyId, List<Long> awardIdList) {
        List<StrategyRule> strategyRuleList = strategyRuleDao.queryRuleLocks(strategyId, awardIdList);
        Map<Long, Integer> resultMap = new HashMap<>();
        for (StrategyRule strategyRule : strategyRuleList) {
            Long awardId = strategyRule.getAwardId();
            Integer ruleValue = Integer.parseInt(strategyRule.getRuleValue());
            resultMap.put(awardId, ruleValue);
        }
        return resultMap;
    }

    /**
     * 根据strategyId查询对应活动结束时间
     *
     * @param strategyId
     * @return
     */
    @Override
    public LocalDateTime queryActivityEndTimeByStrategyId(Long strategyId) {
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivity(RaffleActivity.builder()
                .strategyId(strategyId)
                .build());
        return raffleActivity.getEndDateTime();
    }

    /**
     * 根据awardIds获取StrategyAwardEntity
     *
     * @param awardIds
     * @return
     */
    @Override
    public List<StrategyAwardEntity> queryStrategyAwardEntityByAwardIds(Long strategyId, List<Long> awardIds) {
        List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardEntityByAwardIds(strategyId, awardIds);
        if (strategyAwardList == null || strategyAwardList.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyAwardEntity> strategyAwardEntityList = new ArrayList<>();
        for (StrategyAward strategyAward : strategyAwardList) {
            StrategyAwardEntity strategyAwardEntity = new StrategyAwardEntity();
            BeanUtils.copyProperties(strategyAward, strategyAwardEntity);
            strategyAwardEntityList.add(strategyAwardEntity);
        }
        return strategyAwardEntityList;
    }
}

