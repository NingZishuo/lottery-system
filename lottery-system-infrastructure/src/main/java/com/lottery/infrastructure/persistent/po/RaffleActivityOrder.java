package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *  抽奖活动单 持久化对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleActivityOrder {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商品sku
     */
    private Long sku;

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
     * 订单ID
     */
    private String orderId;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 总次数
     */
    private Integer totalCount;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 订单状态
     */
    private String state;

    /**
     * 防重ID
     */
    private String outBusinessNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
