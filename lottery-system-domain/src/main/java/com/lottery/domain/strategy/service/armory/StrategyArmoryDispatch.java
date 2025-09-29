package com.lottery.domain.strategy.service.armory;

import com.lottery.domain.strategy.model.entity.StrategyAwardEntity;
import com.lottery.domain.strategy.model.entity.StrategyEntity;
import com.lottery.domain.strategy.model.entity.StrategyRuleEntity;
import com.lottery.domain.strategy.repository.IStrategyRepository;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

/**
 * 策略装配库(兵工厂)、负责初始化策略计算
 */
@Service
@Slf4j
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Autowired
    private IStrategyRepository strategyRepository;

    /**
     * 根据strategyId装配对应获奖策略
     * 假如有保底机制 也会进行对应装配
     * 再拓展一点 比如可以指定内部号
     *
     * @param strategyId strategyId
     */
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardEntityList(strategyId);

        if (strategyAwardEntities == null || strategyAwardEntities.isEmpty()) {
            throw new RuntimeException("strategyAwardEntities为空,无法装配策略");
        }
        //挨个将库存写入redis
        this.cacheStrategyAwardListCount(strategyAwardEntities);
        //2.装配特定strategyAwardEntities
        this.assembleLotteryStrategy(strategyId.toString(), strategyAwardEntities);

        //3.权重的规则配置
        StrategyEntity strategyEntity = strategyRepository.queryStrategy(strategyId);
        //4.rule_weight 存在rule_weight这种保底机制才去配置 不存在你配置啥呢
        String ruleWeight = strategyEntity.getRuleWeightStr();
        if (ruleWeight == null || ruleWeight.isEmpty()){
            return true;
        }
        //5.确定rule_weight已经存在
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId,ruleWeight);
        if (strategyRuleEntity == null) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        //6.获取保底机制所需要的数据
        //4000:102,103,104,105
        //5000:102,103,104,105,106,107
        //6000:102,103,104,105,106,107,108,109
        Map<String, List<Long>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        if (ruleWeightValues == null || ruleWeightValues.isEmpty()) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_FORMAT_IS_WRONG.getCode(),ResponseCode.STRATEGY_RULE_WEIGHT_FORMAT_IS_WRONG.getInfo());
        }
        for (String key : ruleWeightValues.keySet()) {
            List<Long> awardIds = ruleWeightValues.get(key);
            int i = 0;
            int j = 0;
            List<StrategyAwardEntity> strategyAwardEntityList = new ArrayList<>();
            while(i < awardIds.size() && j < strategyAwardEntities.size()) {
                if (awardIds.get(i).equals(strategyAwardEntities.get(j).getAwardId())) {
                    i++;
                    strategyAwardEntityList.add(strategyAwardEntities.get(j));
                }
                j++;
            }
            this.assembleLotteryStrategy(strategyId+":pity:"+key, strategyAwardEntityList);
        }
        return true;
    }

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        return this.assembleLotteryStrategy(strategyId);
    }

    private void cacheStrategyAwardListCount(List<StrategyAwardEntity> strategyAwardEntities) {
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            strategyRepository.cacheStrategyAwardCount(strategyAwardEntity.getStrategyId()
                    ,strategyAwardEntity.getAwardId()
                    ,strategyAwardEntity.getAwardCount());
        }
    }

    /**
     * 装配特定List<StrategyAwardEntity> 而并非装配所有奖品
     *
     * @param key 用作key来识别
     * @param strategyAwardEntities 需要装配的list数组
     */
    public void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        //1.获取概率中最长的位数 0.1 0.28 0.117 0.2834 获取0.2834
        Integer maxScaleAwardRate = strategyAwardEntities.stream()
                .map(strategyAwardEntity -> strategyAwardEntity.getAwardRate().stripTrailingZeros().scale())
                .max(Integer::compare).get();
        //2.构建单位：10 的 maxScaleAwardRate 次幂
        //3.假如有如下概率 0.1 0.28 0.117 0.2834 很明显 只要构建出来10000这个能兜住0.2834的数字就行了
        BigDecimal unit = new BigDecimal(10).pow(maxScaleAwardRate);
        //4.让每个概率乘unit  以此获得每个奖品该有多少个
        List<Long> strategyAwardSearchRateTables = new ArrayList<>();
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Long awardId = strategyAwardEntity.getAwardId();
            BigDecimal rateRange = unit.multiply(strategyAwardEntity.getAwardRate());
            for (int i = 0; i < rateRange.intValueExact(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }
        //5.总的抽奖范围的大小
        Integer sumRateRange = strategyAwardSearchRateTables.size();
        //打乱
        Collections.shuffle(strategyAwardSearchRateTables);
        //6.按照打乱的顺序放入map
        HashMap<Integer, Long> shuffleStrategyAwardSearchRateTable = new HashMap<>(sumRateRange);
        for (int i = 0; i < sumRateRange; i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }
        //7.存储到Redis
        strategyRepository.storeStrategyAwardSearchRateTable(key, sumRateRange, shuffleStrategyAwardSearchRateTable);
    }


    /**
     * 得到一个随机的抽奖id
     *
     * @param strategyId
     * @return
     */
    @Override
    public Long getRandomAwardId(Long strategyId) {
        int sumRateRange = strategyRepository.getSumRateRange(strategyId.toString());
        return strategyRepository.getStrategyAwardAssemble(strategyId.toString(), new SecureRandom().nextInt(sumRateRange));
    }

    /**
     * 得到一个随机的抽奖id 保底版
     * @param strategyId which strategy
     * @param ruleWeightValue pity value
     * @return
     */
    @Override
    public Long getRandomAwardId(Long strategyId, int ruleWeightValue) {
        String key = strategyId+":pity:"+ruleWeightValue;
        int sumRateRange = strategyRepository.getSumRateRange(key);
        return strategyRepository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(sumRateRange));
    }

    /**
     * 判断是否存在某一抽数的保底
     * @param strategyId which strategy
     * @param ruleWeightValue pity value
     * @return
     */
    @Override
    public Boolean isRuleWeightValueExists(Long strategyId, int ruleWeightValue) {
        String key = strategyId+":pity:"+ruleWeightValue;
        return strategyRepository.isRuleWeightValueExists(key);
    }

}
