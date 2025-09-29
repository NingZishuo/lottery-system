package com.lottery.domain.award.service.distribute.impl;

import com.lottery.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.lottery.domain.award.model.entity.AwardEntity;
import com.lottery.domain.award.model.entity.CreditAccountEntity;
import com.lottery.domain.award.model.entity.DistributeAwardEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;
import com.lottery.domain.award.model.valobj.AwardStateVO;
import com.lottery.domain.award.repository.IAwardRepository;
import com.lottery.domain.award.service.distribute.IDistributeAward;
import com.lottery.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 用户积分的发放实现
 */
@Service("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Autowired
    private IAwardRepository awardRepository;

    /**
     * user_credit_random 发放奖励
     *
     * @param distributeAwardEntity
     */
    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        Long awardId = distributeAwardEntity.getAwardId();
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if (StringUtils.isBlank(awardConfig)) {
            //奖品配置为null 则用默认配置
            AwardEntity awardEntity = awardRepository.queryAward(awardId);
            awardConfig = awardEntity.getAwardConfig();
        }

        String[] split = awardConfig.split(Constants.SPLIT);
        if (split.length != 2) {
            throw new RuntimeException("积分范围配置错误:" + awardConfig);
        }

        BigDecimal min = new BigDecimal(split[0]);
        BigDecimal max = new BigDecimal(split[1]);

        BigDecimal randomCredit = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        randomCredit = randomCredit.setScale(2, BigDecimal.ROUND_DOWN);


        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(distributeAwardEntity.getUserId())
                //其实只要orderId一个就行了 因为连awardState在sql操作里也是写死的
                .orderId(distributeAwardEntity.getOrderId())
                .awardState(AwardStateVO.completed)
                .build();

        CreditAccountEntity creditAccountEntity = CreditAccountEntity.builder()
                .userId(distributeAwardEntity.getUserId())
                .randomCredit(randomCredit)
                .build();

        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .creditAccountEntity(creditAccountEntity)
                .build();

        //存储聚合对象
        awardRepository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);

    }

}
