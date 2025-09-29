package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.UserAwardRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户中奖记录表
 */
@Mapper
public interface IUserAwardRecordDao {

    /**
     * 综合插入
     * @param userAwardRecord
     */
    void insert(UserAwardRecord userAwardRecord);


    Integer updateAwardRecordCompletedState(UserAwardRecord userAwardRecord);

    /**
     * 综合查询
     */
    List<UserAwardRecord> queryUserAwardRecordList(UserAwardRecord userAwardRecord);
}
