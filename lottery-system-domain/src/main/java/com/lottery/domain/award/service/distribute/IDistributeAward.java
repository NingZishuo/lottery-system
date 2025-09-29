package com.lottery.domain.award.service.distribute;

import com.lottery.domain.award.model.entity.DistributeAwardEntity;

/**
 * 分发奖品接口
 */
public interface IDistributeAward {

     /**
      * 发放奖品
      * @param distributeAwardEntity
      */
     void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);

}
