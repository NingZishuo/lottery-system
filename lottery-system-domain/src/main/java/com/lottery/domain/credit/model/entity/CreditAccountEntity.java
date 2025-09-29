package com.lottery.domain.credit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户积分实体对象
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
     * 调额积分
     */
    private BigDecimal adjustAmount;

}
