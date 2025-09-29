package com.lottery.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  奖品状态枚举值对象 【值对象，用于描述对象属性的值，一个对象中，一个属性，有多个状态值。】
 */
@Getter
@AllArgsConstructor
public enum AwardStateVO {

    create("create", "创建"),
    completed("completed", "发奖完成"),
    ;

    private final String code;
    private final String desc;

}
