package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户抽奖记录请求对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordRequestDTO {
    /**
     * 活动Id
     */
    private Long activityId;
    /**
     * 用户Id
     */
    private String userId;
}
