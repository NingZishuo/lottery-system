package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivityAccountDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 抽奖活动账户表-日次数
 */
@Mapper
public interface IRaffleActivityAccountDayDao {
    /**
     * 综合查询
     * @param raffleActivityAccountDay
     * @return
     */
    RaffleActivityAccountDay queryRaffleActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    /**
     * 扣减日剩余抽奖次数
     * @param raffleActivityAccountDay
     * @return
     */
    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDay);

    /**
     * 扣减日剩余抽奖次数  指定扣减次数
     * @param raffleActivityAccountDay
     * @return
     */
    int updateActivityAccountDaySpecifiedSubtractionQuota(@Param("raffleActivityAccountDay") RaffleActivityAccountDay raffleActivityAccountDay,@Param("subtractor") Integer subtractor);

    /**
     * 综合插入
     * @param raffleActivityAccountDay
     */
    void insert(RaffleActivityAccountDay raffleActivityAccountDay);

    /**
     * 为日账户充值
     * @param raffleActivityAccountDay
     */
    int addAccountDayQuota(RaffleActivityAccountDay raffleActivityAccountDay);
}
