package com.lottery.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户账户情况
 */
@Getter
@AllArgsConstructor
public enum AccountStatusVO {

    OPEN("open", "开启"),
    CLOSE("close", "冻结"),
            ;

    private final String code;
    private final String info;
}
