package com.lottery.trigger.api.dto;

import lombok.Data;

/**
 * 前端请求对象
 */
@Data

public class RaffleStrategyRequestDTO {

    // 抽奖策略ID
    private Long strategyId;

    // 用户ID
    private String userId;

}
