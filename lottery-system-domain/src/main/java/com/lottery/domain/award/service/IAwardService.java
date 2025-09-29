package com.lottery.domain.award.service;

import com.lottery.domain.award.model.entity.DistributeAwardEntity;
import com.lottery.domain.award.model.entity.ShowAwardRecordEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;

import java.util.List;

/**
 * 中奖(发奖)奖品服务接口
 */
public interface IAwardService {

    /**
     * 保存用户中奖记录 报废抽奖单
     * @param userAwardRecordEntity
     */
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 1.批量插入中奖记录 2.报废一张抽奖单
     */
    void saveUserAwardRecordList(List<UserAwardRecordEntity> userAwardRecordEntityList);

    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

    /**
     * 显示用户中奖记录 只显示最近的30条
     * @param showAwardRecordEntity
     * @return
     */
    List<UserAwardRecordEntity> showUserAwardRecordList(ShowAwardRecordEntity showAwardRecordEntity);
}
