package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  活动抽奖返回对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActivityDrawResponseDTO {

    // 奖品ID
    private Long awardId;
    // 奖品标题
    private String awardTitle;
    //奖品配置
    private String awardConfig;
    // 排序编号【策略奖品配置的奖品顺序编号】
    private Integer awardIndex;



}
