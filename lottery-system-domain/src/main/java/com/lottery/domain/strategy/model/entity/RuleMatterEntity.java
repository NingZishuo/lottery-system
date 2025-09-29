package com.lottery.domain.strategy.model.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 规则实体对象,服务过滤规则
 * 里面是必要参数信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleMatterEntity {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 策略Id
     */
    private Long strategyId;
    /**
     * 奖品Id
     */
    private Long awardId;
    /**
     * 抽奖规则类型
     */
    private String ruleModel;


}
