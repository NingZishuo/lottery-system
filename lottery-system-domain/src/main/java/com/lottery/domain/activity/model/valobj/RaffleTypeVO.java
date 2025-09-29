package com.lottery.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举值对象（用于描述对象属性的值，如枚举，不影响数据库操作的对象，无生命周期）
 */
@Getter
@AllArgsConstructor
public enum RaffleTypeVO {

    single("single",1 ,"单抽"),
    ten("ten", 10,"十连抽"),
    ;

    private final String code;
    private final Integer subtractor;
    private final String desc;

}
