package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 抽奖活动账户表
 */
@Mapper
public interface IRaffleActivityAccountDao {

    void insert(RaffleActivityAccount raffleActivityAccount);

    /**
     * 给账户添加抽奖机会
     * @param raffleActivityAccount
     */
    int addAccountQuota(RaffleActivityAccount raffleActivityAccount);


    /**
     * 抽奖机会扣减
     * @param raffleActivityAccount
     */
    int updateActivityAccountSubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    /**
     * 抽奖机会扣减 指定扣减次数
     * @param raffleActivityAccount
     */
    int updateActivityAccountSpecifiedSubtractionQuota(@Param("raffleActivityAccount") RaffleActivityAccount raffleActivityAccount,@Param("subtractor") Integer subtractor);

    /**
     * 综合查询
     * @param raffleActivityAccount
     * @return
     */
    RaffleActivityAccount queryRaffleActivityAccount(RaffleActivityAccount raffleActivityAccount);

}
