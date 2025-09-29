package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 抽奖策略奖品明细配置-概率、规则 DAO
 */
@Mapper
public interface IStrategyAwardDao {

    /**
     * 根据strategyId查询
     * @param strategyId
     * @return
     */
    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    List<StrategyAward> queryStrategyAwardList();

    /**
     * 综合条件查询
     * @param strategyAward
     * @return
     */
    StrategyAward queryStrategyAward(StrategyAward strategyAward);

    /**
     * 更新库存信息
     * @param strategyAward
     */
    void updateStrategyAwardStock(StrategyAward strategyAward);

    /**
     * 根据awardIds 补全信息
     * @param awardIds
     * @return
     */
    List<StrategyAward> queryStrategyAwardEntityByAwardIds(@Param("strategyId") Long strategyId, @Param("awardIds") List<Long> awardIds);
}
