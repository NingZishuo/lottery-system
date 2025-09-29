package com.lottery.domain.activity.model.entity;

import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import com.lottery.domain.activity.model.valobj.UserRaffleOrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户兑换抽奖单的实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRaffleOrderEntity {


    /**
     * 用户ID
     */
    private String userId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 种类:single-单抽、ten-十连抽
     */
    private RaffleTypeVO raffleType;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 下单时间
     */
    private LocalDateTime orderTime;
    /**
     * 订单状态；create-创建、used-已使用、cancel-已作废
     */
    private UserRaffleOrderStateVO orderState;



}
