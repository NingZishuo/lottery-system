package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 抽奖策略规则，权重配置，查询N次抽奖可解锁奖品范围，应答对象
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyRuleWeightResponseDTO {

    // 用户在该活动下完成的已抽奖次数
    private Integer userActivityAccountTotalUsedCount;

    //权重及其对应抽奖范围
    private Map<String,List<StrategyAward>> ruleWeightAndStrategyAwardList;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAward {
        // 奖品ID
        private Long awardId;
        // 奖品标题
        private String awardTitle;
    }


}
