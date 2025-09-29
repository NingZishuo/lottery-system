package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖策略 DAO
 */
@Mapper
public interface IStrategyDao {

    /**
     * 根据strategyId查询Strategy
     * @param strategyId
     * @return
     */
    Strategy queryStrategyByStrategyId(Long strategyId);
}
