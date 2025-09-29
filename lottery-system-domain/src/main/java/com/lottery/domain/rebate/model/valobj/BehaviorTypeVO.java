package com.lottery.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行为类型枚举值对象
 */
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign", "签到"),
    PAY("pay", "支付完成"),
    ;

    private final String code;
    private final String info;

    public static BehaviorTypeVO valueOfLowerCase(String input) {
        if (input == null) {
            return null;
        }
        return BehaviorTypeVO.valueOf(input.toUpperCase());
    }

}
