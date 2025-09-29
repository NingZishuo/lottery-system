package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户抽奖订单表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRaffleOrder {

    /**
     * 自增ID
     */
    private Long id;
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
    private String raffleType;
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
    private String orderState;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
