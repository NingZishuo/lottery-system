package com.lottery.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  抽奖奖品实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {
    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Long awardId;
    /**
     * 奖品标题（名称）
     */
    private String awardTitle;
    /**
     * 奖品配置信息
     */
    private String awardConfig;
    /**
     * 奖品顺序号
     */
    private Integer sort;


}
