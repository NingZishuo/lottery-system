package com.lottery.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 黑名单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyBlackListEntity {
  /**
   * 奖品ID (黑名单固定奖品)
   */
  private Long awardId;
  /**
   * 用户ID
   */
  private String userId;


}
