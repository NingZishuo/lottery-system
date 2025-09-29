package com.lottery.domain.strategy.model.entity;

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
public class StrategyWhiteListEntity {

  /**
   * 最低保底奖励门槛
   */
  private Integer ruleWeight;
  /**
   * 用户ID
   */
  private String userId;

}
