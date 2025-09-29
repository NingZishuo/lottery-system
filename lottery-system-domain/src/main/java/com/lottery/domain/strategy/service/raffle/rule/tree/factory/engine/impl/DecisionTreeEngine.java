package com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine.impl;

import com.lottery.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.lottery.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.lottery.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.lottery.domain.strategy.model.valobj.RuleTreeVO;
import com.lottery.domain.strategy.service.raffle.rule.tree.ILogicTreeNode;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.DefaultTreeFactory;
import com.lottery.domain.strategy.service.raffle.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 决策树引擎
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {


    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }


    @Override
    public DefaultTreeFactory.TreeActionEntity process(String userId, Long strategyId, Long awardId,Integer completedDrawCount) {
        DefaultTreeFactory.TreeActionEntity treeActionEntity = null;
        //获取基础信息
        String currentTreeNodeStr = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        RuleTreeNodeVO currentTreeNodeVO = treeNodeMap.get(currentTreeNodeStr);


        while (currentTreeNodeVO != null) {
            log.info("决策树ruleMode:{},ruleDesc:{}", currentTreeNodeVO.getRuleModel(), currentTreeNodeVO.getRuleDesc());
            ILogicTreeNode iLogicTreeNode = logicTreeNodeGroup.get(currentTreeNodeVO.getRuleModel());
            treeActionEntity = iLogicTreeNode.logic(userId, strategyId, awardId,completedDrawCount);

            RuleLogicCheckTypeVO ruleLogicCheckType = treeActionEntity.getRuleLogicCheckType();
            //这里需要找到下一个TreeNode 假如找不到就可以返回最终结果了
            currentTreeNodeStr = this.nextTreeNode(ruleLogicCheckType.getCode(), currentTreeNodeVO.getTreeNodeLineVOList());

            currentTreeNodeVO = treeNodeMap.get(currentTreeNodeStr);

        }

        //返回最终结果
        return treeActionEntity;
    }


    private String nextTreeNode(String Code, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList) {
        for (RuleTreeNodeLineVO ruleTreeNodeLineVO : ruleTreeNodeLineVOList) {
            //决定接下来走哪个逻辑
            if (decisionLogic(Code, ruleTreeNodeLineVO)) {
                return ruleTreeNodeLineVO.getRuleNodeTo();
            }
        }
        return null;
    }


    public boolean decisionLogic(String Code, RuleTreeNodeLineVO ruleTreeNodeLineVO) {
        switch (ruleTreeNodeLineVO.getRuleLimitType()) {
            case EQUAL:
                return Code.equals(ruleTreeNodeLineVO.getRuleLogicCheckType().getCode());
            case GT:
            case LT:
            case GE:
            case LE:
            case ENUM:
            default:
                return false;

        }
    }


}
