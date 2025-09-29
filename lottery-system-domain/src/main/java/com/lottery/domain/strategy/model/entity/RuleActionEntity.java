package com.lottery.domain.strategy.model.entity;

import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则动作实体
 *  注意其中参数是为了后续action作准备的
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {
    /**
     * 策略Id
     */
    private Long strategyId;
    /**
     * 是否接管
     * 其实用data是否==null 也可以判断需要接管吗？
     */
    private RuleLogicCheckTypeVO ruleLogicCheckType;
    //例: 抽奖前 ruleModel = black_list
    //         data = RaffleBeforeEntity(里面写好参数)
    /**
     * 规则模型
     * 你这个后续操作的rule是哪个 黑名单还是保底?
     */
    private String ruleModel;

    /**
     * 三种抽奖实体
     * 其中属性为了后续操作所需要的参数
     */
    private T data;



    static public class RaffleEntity {


    }

    /**
     * 在下面有三种抽奖实体 分别对应抽奖前,中,后
     * 其中属性是为了后续真正操作(action)做铺垫(即传参)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity {

        //TODO其实pityLimit和lowestRuleWeight 可以融合的  用一个就行了
        /**
         * 保底用的是哪个保底:4000 5000 6000
         */
        private Integer pityLimit;
        /**
         * 黑名单用户直接发放奖励
         */
        private Long awardId;
        /**
         * 白名单用户的最低奖励门槛
         */
        private Integer lowestRuleWeight;
    }

    @Data
    @Builder
    //@AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleCenterEntity extends RaffleEntity {

    }

    @Data
    @Builder
    //@AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleAfterEntity extends RaffleEntity {

    }


}
