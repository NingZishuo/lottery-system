package com.lottery.infrastructure.persistent.dao;


import com.lottery.infrastructure.persistent.po.StrategyWhiteList;
import org.apache.ibatis.annotations.Mapper;

/**
 * 白名单 DAO
 */
@Mapper
public interface IStrategyWhiteListDao {

    /**
     * 综合条件查询
     * @param strategyWhiteList
     * @return
     */
    StrategyWhiteList queryStrategyWhiteList(StrategyWhiteList strategyWhiteList);


}
