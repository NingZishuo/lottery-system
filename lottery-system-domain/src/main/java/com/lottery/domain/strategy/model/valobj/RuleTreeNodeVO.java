package com.lottery.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 规则树节点对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {

    /**
     * 规则树ID
     */
    private String treeId;
    /**
     * 规则Model:作为key得到真正的删选树节点
     */
    private String ruleModel;
    /**
     * 对于规则模型的描述
     */
    private String ruleDesc;
    /**
     * 规则连线
     */
    private List<RuleTreeNodeLineVO> treeNodeLineVOList;

}
