package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端请求对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyAwardRequestDTO {

    @Deprecated
    //策略ID
    private Long strategyId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private String userId;

}
