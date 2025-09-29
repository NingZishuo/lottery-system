package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 抽奖策略奖品明细配置-概率、规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAward {


    /**
     * 自增ID
     */
    private Long id;
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖奖品ID-内部流转使用
     */
    private Long awardId;
    /**
     * 抽奖奖品标题
     */
    private String awardTitle;
    /**
     * 抽奖奖品副标题
     */
    private String awardSubtitle;
    /**
     * 抽奖奖品总量
     */
    private Long awardCount;
    /**
     * 抽奖奖品剩余
     */
    private Long awardCountSurplus;
    /**
     * 奖品中奖概率
     */
    private BigDecimal awardRate;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 规则模型
     */
    private String ruleModels;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
