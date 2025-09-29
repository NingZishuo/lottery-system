package com.lottery.domain.rebate.service;

import com.lottery.domain.rebate.model.entity.BehaviorEntity;
import com.lottery.domain.rebate.model.entity.BehaviorRebateOrderEntity;

import java.util.List;

/**
 * 行为返利服务接口
 */
public interface IBehaviorRebateService {

    /**
     * 创建用户行为订单
     * @param behaviorEntity
     * @return 单号id 可能多个
     */
    List<String> createBehaviorRebateOrder(BehaviorEntity behaviorEntity);

    /**
     * 查询某用户是否已经进行某个行为 如签到
     * @param behaviorEntity
     * @return
     */
    List<BehaviorRebateOrderEntity> queryUserRebateOrderByBehaviorType(BehaviorEntity behaviorEntity);
}
