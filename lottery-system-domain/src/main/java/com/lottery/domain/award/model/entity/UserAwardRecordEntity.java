package com.lottery.domain.award.model.entity;

import com.lottery.domain.award.model.valobj.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户中奖记录实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordEntity {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖单ID
     */
    private String orderId;
    /**
     * 奖品ID
     */
    private Long awardId;
    /**
     * 奖品标题（名称）
     */
    private String awardTitle;
    /**
     * 奖品配置
     */
    private String awardConfig;
    /**
     * 中奖时间
     */
    private LocalDateTime awardTime;
    /**
     * 奖品状态；create-创建、completed-发奖完成
     */
    private AwardStateVO awardState;




    /**
     * 奖品sort排列 注意! 非数据库字段 单纯是业务需要 所以添加到实体上
     */
    private Integer sort;

}
