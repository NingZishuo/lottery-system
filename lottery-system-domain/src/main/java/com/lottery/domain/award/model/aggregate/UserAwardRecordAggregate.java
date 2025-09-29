package com.lottery.domain.award.model.aggregate;

import com.lottery.domain.award.model.entity.TaskEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 中奖(发奖)聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordAggregate {
    /**
     * 消息实体
     */
    private TaskEntity taskEntity;

    /**
     * 用户中奖记录实体
     */
    private UserAwardRecordEntity userAwardRecordEntity;
}
