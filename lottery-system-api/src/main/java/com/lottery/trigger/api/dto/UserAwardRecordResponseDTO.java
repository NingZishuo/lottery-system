package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户抽奖记录应答对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordResponseDTO {
    /**
     * 活动Id
     */
    private Long activityId;
    /**
     * 奖品标题（名称）
     */
    private String awardTitle;
    /**
     * 中奖时间
     */
    private LocalDateTime awardTime;
    /**
     * 奖品状态；create-创建、completed-发奖完成
     */
    private String awardState;

}
