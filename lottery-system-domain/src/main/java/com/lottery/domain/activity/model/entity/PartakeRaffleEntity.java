package com.lottery.domain.activity.model.entity;

import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参与抽奖活动实体对象,为了生成抽奖单
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartakeRaffleEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 抽奖种类 single-单抽 or ten-十连抽
     */
    private RaffleTypeVO raffleType;


}
