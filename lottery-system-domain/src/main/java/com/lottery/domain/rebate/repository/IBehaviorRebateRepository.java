package com.lottery.domain.rebate.repository;


import com.lottery.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.lottery.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.lottery.domain.rebate.model.valobj.BehaviorTypeVO;
import com.lottery.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.List;

/**
 * 行为返利服务仓储接口
 */
public interface IBehaviorRebateRepository {

    /**
     * 查询是否存在对应的日行为返利配置
     * @param behaviorTypeVO
     * @return
     */
    List<DailyBehaviorRebateVO> queryDailyBehaviorRebate(BehaviorTypeVO behaviorTypeVO);

    /**
     * 保存聚合对象
     * @param behaviorRebateAggregates
     */
    void saveUserRebateRecord(List<BehaviorRebateAggregate> behaviorRebateAggregates);

    /**
     * 通过防重ID查询是否已经存在对应返利订单
     *
     * @param userId
     * @param outBusinessNoList
     * @return
     */
    List<BehaviorRebateOrderEntity> queryUserRebateOrderByOutBusinessNoList(String userId, List<String> outBusinessNoList);
}
