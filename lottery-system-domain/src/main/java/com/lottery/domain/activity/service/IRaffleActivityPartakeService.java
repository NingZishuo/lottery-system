package com.lottery.domain.activity.service;

import com.lottery.domain.activity.model.entity.PartakeRaffleEntity;
import com.lottery.domain.activity.model.entity.UserRaffleOrderEntity;
import com.lottery.domain.activity.model.valobj.RaffleTypeVO;

/**
 * 抽奖活动参与服务
 */
public interface IRaffleActivityPartakeService {

    /**
     * 使用抽奖机会->创建抽奖单->使用抽奖单抽奖->存在未使用抽奖单则用之前未使用的抽奖单
     * @return 用户抽奖单
     */
    UserRaffleOrderEntity createPartakeOrder(PartakeRaffleEntity partakeRaffleEntity);

    /**
     * 重载而已
     */
    UserRaffleOrderEntity createPartakeOrder(String userId, Long activityId, RaffleTypeVO orderType);


    /**
     * 查询某个用户在某个活动的已抽奖次数
     * @param userId
     * @param activityId
     * @return
     */
    Integer queryCompletedDrawCount(String userId, Long activityId);
}
