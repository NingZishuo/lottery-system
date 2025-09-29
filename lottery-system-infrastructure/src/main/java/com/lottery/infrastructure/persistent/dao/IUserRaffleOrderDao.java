package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.UserRaffleOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户抽奖订单表
 */
@Mapper
public interface IUserRaffleOrderDao {
    /**
     * 查询未使用抽奖单
     * @param userRaffleOrder
     * @return
     */
    List<UserRaffleOrder> queryNoUseRaffleOrder(UserRaffleOrder userRaffleOrder);

    /**
     * 综合插入
     * @param build
     */
    void insert(UserRaffleOrder build);


    /**
     * 更新抽奖单为used
     * @param userRaffleOrder
     * @return
     */
    int updateUserRaffleOrderStateUsed(UserRaffleOrder userRaffleOrder);

    /**
     * 更新抽奖单为cancel
     * @param userRaffleOrder
     * @return
     */
    int updateUserRaffleOrderStateCancel(UserRaffleOrder userRaffleOrder);
}
