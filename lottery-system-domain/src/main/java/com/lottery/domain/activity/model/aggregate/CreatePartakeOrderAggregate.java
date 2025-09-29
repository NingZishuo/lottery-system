package com.lottery.domain.activity.model.aggregate;

import com.lottery.domain.activity.model.entity.RaffleActivityAccountDayEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityAccountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityAccountMonthEntity;
import com.lottery.domain.activity.model.entity.UserRaffleOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖单下单聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {

    /**
     * 用户兑换抽奖单的实体对象
     */
    private UserRaffleOrderEntity userRaffleOrderEntity;
    /**
     * 活动账户实体对象
     */
    private RaffleActivityAccountEntity raffleActivityAccountEntity;
    /**
     * 活动账户（日）实体对象
     */
    private Boolean isExistAccountDay = null;

    private RaffleActivityAccountDayEntity raffleActivityAccountDayEntity;
    /**
     * 活动账户（月）实体对象
     */
    private Boolean isExistAccountMonth = null;

    private RaffleActivityAccountMonthEntity raffleActivityAccountMonthEntity;
}
