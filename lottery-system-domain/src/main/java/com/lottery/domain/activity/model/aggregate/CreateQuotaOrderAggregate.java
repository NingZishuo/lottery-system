package com.lottery.domain.activity.model.aggregate;

import com.lottery.domain.activity.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 充值单聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuotaOrderAggregate {

    /**
     * 是否存在活动账户实体对象
     */
    private Boolean isExistAccount = null;
    /**
     * 活动账户实体
     */
    private RaffleActivityAccountEntity raffleActivityAccountEntity;
    /**
     * 活动账户月实体
     */
    private RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity;
    /**
     * 活动账户日实体
     */
    private RaffleActivityAccountDayEntity raffleActivityAccountDayEntity;
    /**
     * 活动订单实体
     */
    private RaffleActivityOrderEntity raffleActivityOrderEntity;

}
