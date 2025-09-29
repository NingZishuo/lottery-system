package com.lottery.domain.award.model.aggregate;

import com.lottery.domain.award.model.entity.CreditAccountEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发放奖品聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiveOutPrizesAggregate {

    /**
     * 用户发奖记录
     */
    private UserAwardRecordEntity userAwardRecordEntity;
    /**
     * 用户积分奖品
     */
    private CreditAccountEntity creditAccountEntity;


}
