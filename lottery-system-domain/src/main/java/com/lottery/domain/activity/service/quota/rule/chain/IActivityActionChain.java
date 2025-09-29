package com.lottery.domain.activity.service.quota.rule.chain;


import com.lottery.domain.activity.model.entity.RaffleActivityCountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityEntity;
import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;

/**
 * 责任链接口
 */
public interface IActivityActionChain extends IActivityActionChainArmory {

     /**
     * 活动责任链接口
     * @param raffleActivitySkuEntity
     * @param raffleActivityEntity
     * @param raffleActivityCountEntity
     * @return
     */
     boolean action(RaffleActivitySkuEntity raffleActivitySkuEntity, RaffleActivityEntity raffleActivityEntity, RaffleActivityCountEntity raffleActivityCountEntity);


}
