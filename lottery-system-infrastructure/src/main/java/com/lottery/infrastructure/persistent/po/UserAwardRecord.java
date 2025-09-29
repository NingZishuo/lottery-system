package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户中奖记录表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAwardRecord {

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
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖订单ID【作为幂等使用】
     */
    private String orderId;
    /**
     * 奖品ID
     */
    private Long awardId;
    /**
     * 奖品标题（名称）
     */
    private String awardTitle;

    /**
     * 奖品配置
     */
    private String awardConfig;
    /**
     * 中奖时间
     */
    private LocalDateTime awardTime;
    /**
     * 奖品状态；create-创建、completed-发奖完成
     */
    private String awardState;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
