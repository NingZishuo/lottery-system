package com.lottery.domain.strategy.model.entity;


import com.lottery.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 策略实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyEntity {
    /**
     * 抽奖策略ID
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

    public String[] ruleModels(){
        if (StringUtils.isBlank(ruleModels)) {
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }


    public String getRuleWeightStr() {
        String[] ruleModels = ruleModels();
        if (ruleModels != null) {
            for (String ruleModel : ruleModels) {
                if ("rule_weight".equals(ruleModel)) {
                    return ruleModel;
                }
            }
        }
       return null;
    }


}
