package com.lottery.domain.rebate.model.entity;

import com.lottery.domain.rebate.model.valobj.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorEntity {
    /**-
     * 用户ID
     */
    private String userId;
    /**
     * 行为类型
     */
    private BehaviorTypeVO behaviorTypeVO;

}
