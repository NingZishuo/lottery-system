package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * 规则树表DAO
 */
@Mapper
public interface IRuleTreeDao {

    RuleTree queryRuleTreeByTreeId(String treeId);


    RuleTree queryRuleTree(RuleTree ruleTree);

}
