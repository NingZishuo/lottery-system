package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *用户行为返利流水订单表
 */
@Mapper
public interface IUserBehaviorRebateOrderDao {

    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);

    /**
     * 判断用户是否存在对应业务ID
     * @param userId
     * @param outBusinessNoList
     * @return
     */
    List<UserBehaviorRebateOrder> queryRebateOrderByoutBusinessNoList(@Param("userId") String userId,@Param("outBusinessNoList") List<String> outBusinessNoList);
}
