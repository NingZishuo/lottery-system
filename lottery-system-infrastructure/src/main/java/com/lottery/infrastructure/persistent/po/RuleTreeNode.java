package com.lottery.infrastructure.persistent.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规则树节点
 */
@Data
public class RuleTreeNode {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 规则树ID
     */
    private String treeId;
    /**
     * 规则Key
     */
    private String ruleModel;
    /**
     * 规则描述
     */
    private String ruleDesc;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
