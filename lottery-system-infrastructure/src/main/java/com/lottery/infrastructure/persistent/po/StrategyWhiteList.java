package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 白名单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyWhiteList {

  /**
   * 自增ID
   */
  private Long id;
  /**
   * 抽奖策略ID
   */
  private Long strategyId;
  /**
   * 最低保底奖励门槛
   */
  private Integer ruleWeight;
  /**
   * 用户ID
   */
  private String userId;


}
