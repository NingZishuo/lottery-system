package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 *  用户积分账户
 */
@Mapper
public interface IUserCreditAccountDao {

    /**
     * 插入
     * @param userCreditAccountReq
     */
    void insert(UserCreditAccount userCreditAccountReq);

    /**
     * 添加积分和剩余积分
     * @param userCreditAccountReq
     * @return
     */
    int updateAddAmount(UserCreditAccount userCreditAccountReq);


    /**
     * 根据userId查询账户可用积分
     */
    BigDecimal queryCreditAccountAvailableAmount(String userId);


    /**
     * 扣减账户积分 只扣减可用积分
     * @param userCreditAccount
     */
    void updateSubtractionAmount(UserCreditAccount userCreditAccount);
}
