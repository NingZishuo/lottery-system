package com.lottery.infrastructure.persistent.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 抽奖策略
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {


    /**
     * 自增ID
     */
    private Long id;
    /**
     * 抽奖策略IDID
     */
    private Long strategyId;
    /**
     * 抽奖策略描述
     */
    private String strategyDesc;
    /**
     * 策略模型
     */
    private String ruleModels;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建时间
     */
    private LocalDateTime updateTime;


}
