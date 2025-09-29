package com.lottery.domain.strategy.model.entity;

import com.lottery.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyRuleEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖奖品ID
     */
    private Long awardId;
    /**
     * 抽奖规则类型[1-策略规则 2-奖品规则]
     */
    private Long ruleType;
    /**
     * 抽奖规则类型[rule_lock]
     *
     */
    private String ruleModel;
    /**
     * 抽奖规则比值
     * 4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     */
    private String ruleValue;
    /**
     * 抽奖规则描述
     */
    private String ruleDesc;


    public Map<String, List<Long>> getRuleWeightValues(){
        if (!"rule_weight".equals(ruleModel)){
            return null;
        }
        Map<String, List<Long>> ruleWeightValues = new LinkedHashMap<>();
        //保底和保底奖励的范围
        String[] pityAndAwardRangeGroup = ruleValue.split(Constants.SPACE);
        if (pityAndAwardRangeGroup.length == 0){
            return null;
        }
        for (String pityAndAwardRange : pityAndAwardRangeGroup) {
            String[] str = pityAndAwardRange.split(Constants.COLON);
            if (str.length != 2){
                return null;
            }
            String pity = str[0];
            String awardRange = str[1];
            List<Long> awardIds = new ArrayList<>();
            for (String awardId : awardRange.split(Constants.SPLIT)) {
                awardIds.add(Long.parseLong(awardId));
            }
            ruleWeightValues.put(pity, awardIds);
        }
        return ruleWeightValues;
    }

}
