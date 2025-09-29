package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后端返回对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyAwardResponseDTO {

    /**
     * 奖品ID
     */
    private Long awardId;
    /**
     * 奖品标题
     */
    private String awardTitle;
    /**
     *  奖品副标题【抽奖n次后解锁】
     */
    private String awardSubtitle;
    /**
     * 排序编号
     */
    private Integer sort;
    /**
     * 抽奖n次后解锁那个标识
     */
    private Integer awardRuleLockCount;
    /**
     * 奖品是否解锁 true:已解锁 false:未解锁
     */
    private Boolean isAwardUnLock;
    /**
     * 再抽奖几次就能解锁了
     */
    private Integer waitUnLockCount;



}
