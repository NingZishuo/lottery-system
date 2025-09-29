package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.UserCreditOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分流水单 DAO
 */
@Mapper
public interface IUserCreditOrderDao {

    void insert(UserCreditOrder userCreditOrderReq);

}
