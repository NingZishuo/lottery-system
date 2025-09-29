package com.lottery.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户积分奖品实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 随机分(加到用户积分上)
     */
    private BigDecimal randomCredit;

}
