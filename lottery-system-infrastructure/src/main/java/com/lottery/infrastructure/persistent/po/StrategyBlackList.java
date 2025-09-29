package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 黑名单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyBlackList {

  /**
   * 自增ID
   */
  private Long id;
  /**
   * 抽奖策略ID
   */
  private Long strategyId;
  /**
   * 奖品ID (黑名单固定奖品)
   */
  private Long awardId;
  /**
   * 用户ID
   */
  private String userId;


}
