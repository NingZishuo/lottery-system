package com.lottery.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 展示中奖记录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowAwardRecordEntity {

    /**
     * 活动Id
     */
    private Long activityId;
    /**
     * 用户Id
     */
    private String userId;
}
