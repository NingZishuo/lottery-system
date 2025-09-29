package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后端返回对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyResponseDTO {

    // 奖品ID
    private Long awardId;
    /**
     * 奖品配置信息
     */
    private String awardConfig;
    // 排序编号【策略奖品配置的奖品顺序编号】
    private Integer awardIndex;

}
