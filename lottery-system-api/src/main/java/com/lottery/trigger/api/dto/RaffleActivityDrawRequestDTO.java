package com.lottery.trigger.api.dto;

import lombok.Data;

/**
 * 活动抽奖请求对象
 */
@Data
public class RaffleActivityDrawRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
