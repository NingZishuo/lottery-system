package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivityAccountMonth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 抽奖活动账户表-月次数
 */
@Mapper
public interface IRaffleActivityAccountMonthDao {

    /**
     * 综合查询
     * @param raffleActivityAccountMonth
     * @return
     */
    RaffleActivityAccountMonth queryRaffleActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);

    /**
     * 扣减月剩余抽奖次数
     * @param raffleActivityAccountMonth
     * @return
     */
    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);

    /**
     * 扣减月剩余抽奖次数 指定扣减次数
     * @param raffleActivityAccountMonth
     * @return
     */
    int updateActivityAccountMonthSpecifiedSubtractionQuota(@Param("raffleActivityAccountMonth") RaffleActivityAccountMonth raffleActivityAccountMonth,@Param("subtractor") Integer subtractor);

    /**
     * 综合插入
     * @param raffleActivityAccountMonth
     */
    void insert(RaffleActivityAccountMonth raffleActivityAccountMonth);

    /**
     * 为月账户充值
     * @param raffleActivityAccountMonth
     */
    int addAccountMonthQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
