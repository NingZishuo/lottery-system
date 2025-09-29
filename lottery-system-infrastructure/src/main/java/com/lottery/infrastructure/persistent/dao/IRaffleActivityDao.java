package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 *  抽奖活动表Dao
 */
@Mapper
public interface IRaffleActivityDao {
    /**
     * 综合查询
     * @param raffleActivity
     * @return
     */
    RaffleActivity queryRaffleActivity(RaffleActivity raffleActivity);
}
