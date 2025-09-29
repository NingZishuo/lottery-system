package com.lottery.domain.activity.model.entity;

import com.lottery.domain.activity.model.valobj.ActivityQuotaOrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 抽奖活动单对象(注意这个是充值单)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityOrderEntity {

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
    private ActivityQuotaOrderStateVO state;

    /**
     * 防重ID
     */
    private String outBusinessNo;

}
