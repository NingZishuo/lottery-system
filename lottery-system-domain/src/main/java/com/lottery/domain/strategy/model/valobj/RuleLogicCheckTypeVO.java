package com.lottery.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则过滤校验类型值对象
 */
@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    ALLOW("0000","放行:后续的流程不受规则引擎影响"),
    TAKE_OVER("0001","接管:后续的流程受规则引擎影响"),
    ;
    private final String code;
    private final String info;



}

