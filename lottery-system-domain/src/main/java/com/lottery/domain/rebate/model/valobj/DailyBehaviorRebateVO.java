package com.lottery.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  日常行为返利配置值对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyBehaviorRebateVO {

    /**
     * 行为类型（sign 签到、pay 支付）
     */
    private BehaviorTypeVO behaviorTypeVO;
    /**
     * 返利描述
     */
    private String rebateDesc;
    /**
     * 返利类型（sku 活动库存充值商品、integral 用户活动积分）
     */
    private RebateTypeVO rebateTypeVO;
    /**
     * 返利配置 sku或者直接积分发放
     */
    private String rebateConfig;

}
