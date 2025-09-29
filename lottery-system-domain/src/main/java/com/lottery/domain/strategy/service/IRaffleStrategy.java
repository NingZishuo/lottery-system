package com.lottery.domain.strategy.service;

import com.lottery.domain.strategy.model.entity.RaffleAwardEntity;
import com.lottery.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {

    /**
     *  执行抽奖,返回奖品实体
     *  抽奖要执行很多逻辑,并非test测试一下返回个“奖品id:102”就完事了
     * @param raffleFactorEntity
     * @return 奖品实体
     */
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
