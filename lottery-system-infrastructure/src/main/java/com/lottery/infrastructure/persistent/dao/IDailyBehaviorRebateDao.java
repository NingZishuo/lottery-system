package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *  日常行为返利活动配置
 */
@Mapper
public interface IDailyBehaviorRebateDao {

    /**
     * 综合查询
     * @param dailyBehaviorRebate
     * @return
     */
    List<DailyBehaviorRebate> queryDailyBehaviorRebate(DailyBehaviorRebate dailyBehaviorRebate);

}
