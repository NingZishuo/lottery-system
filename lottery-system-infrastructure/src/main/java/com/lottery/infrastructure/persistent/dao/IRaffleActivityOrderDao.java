package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivityOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 *  抽奖活动单Dao
 */
@Mapper
public interface IRaffleActivityOrderDao {

    /**
     * 插入数据
     *
     * @param raffleActivityOrder
     */
    void insert(RaffleActivityOrder raffleActivityOrder);


    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(String userId);
    /**
     * wait_pay更新到completed
     *
     * @return
     */
    int updateRaffleActivityOrderCompleted(RaffleActivityOrder raffleActivityOrder);

    /**
     * 综合查询
     * @param raffleActivityOrder
     * @return
     */
    RaffleActivityOrder queryRaffleActivityOrder(RaffleActivityOrder raffleActivityOrder);

    /**
     * 查询订单所需支付金额
     * @param outBusinessNo
     * @return
     */
    BigDecimal queryActivityOrderPayAmount(@Param("userId") String userId, @Param("outBusinessNo") String outBusinessNo);

}
