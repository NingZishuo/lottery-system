package com.lottery.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则树节点指向线对象。用于衔接 from->to 节点链路关系
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeLineVO {

    /**
     * 规则树ID
     */
    private String treeId;
    /**
     * 规则Key节点 From
     */
    private String ruleNodeFrom;
    /**
     * 规则Key节点 To
     */
    private String ruleNodeTo;
    /**
     * 限定值要求
     * 限定类型；1:=;2:>;3:<;4:>=;5<=;6:enum[枚举范围]
     * 上一节点和下一节点要怎样运算才能算被筛选出来呢
     *
     */
    private RuleLimitTypeVO ruleLimitType;
    /**
     * 限定值(到下个节点):根据上一节点,来筛选下一个节点
     * 比如上一个节点是ALLOW放行 那么只有标识为ALLOW放行的树节点才会被执行
     */
    private RuleLogicCheckTypeVO ruleLogicCheckType;

}
