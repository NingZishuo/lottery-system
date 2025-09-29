package com.lottery.domain.strategy.model.entity;

import com.lottery.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 策略奖品实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  StrategyAwardEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖奖品ID - 内部流转使用
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
     * 抽奖奖品标题
     */
    private Long awardCount;
    /**
     * 抽奖奖品剩余
     */
    private Integer awardCountSurplus;
    /**
     * 奖品中奖概率
     */
    private BigDecimal awardRate;
    /**
     * 排序
     */
    private Integer sort;
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

    public String getRuleModel(String ruleModel){
        for (String model : ruleModels()) {
            if (model.equals(ruleModel)) {
                return model;
            }
        }
        return null;
    }

}
