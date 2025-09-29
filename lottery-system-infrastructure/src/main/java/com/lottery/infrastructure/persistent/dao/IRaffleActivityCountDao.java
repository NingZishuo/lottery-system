package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖活动次数配置表Dao
 */
@Mapper
public interface IRaffleActivityCountDao {

    /**
     * 综合查询
     * @param raffleActivityCount
     * @return
     */
    RaffleActivityCount queryRaffleActivityCount(RaffleActivityCount raffleActivityCount);
}
