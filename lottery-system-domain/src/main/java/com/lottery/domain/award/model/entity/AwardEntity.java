package com.lottery.domain.award.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品实体表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardEntity {

    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Long awardId;
    /**
     * 奖品对接标识 - 每一个都是一个对应的发奖策略
     */
    private String awardKey;
    /**
     * 奖品配置信息
     */
    private String awardConfig;
    /**
     * 奖品内容描述
     */
    private String awardDesc;


}
