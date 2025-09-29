package com.lottery.infrastructure.persistent.dao;


import com.lottery.infrastructure.persistent.po.StrategyBlackList;
import org.apache.ibatis.annotations.Mapper;

/**
 * 黑名单 DAO
 */
@Mapper
public interface IStrategyBlackListDao {

    /**
     * 综合条件查询
     * @param strategyBlackList
     * @return
     */
    StrategyBlackList queryStrategyBlackList(StrategyBlackList strategyBlackList);


}
