package com.lottery.infrastructure.persistent.dao;



import com.lottery.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 策略规则 DAO
 */
@Mapper
public interface IStrategyRuleDao {
    /**
     * 综合条件查询
     * @param strategyRule
     * @return
     */
    StrategyRule queryStrategyRule(StrategyRule strategyRule);

    /**
     * 查询ruleLock对应的次数锁
     * @param awardIdList
     * @return
     */
    List<StrategyRule> queryRuleLocks(@Param("strategyId") Long strategyId,@Param("awardIdList") List<Long> awardIdList);
}
