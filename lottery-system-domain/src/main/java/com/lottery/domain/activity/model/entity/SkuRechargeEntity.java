package com.lottery.domain.activity.model.entity;


import com.lottery.domain.activity.model.valobj.SkuRechargeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sku充值实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuRechargeEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     *订单ID - 该sku来自哪个返利订单
     */
    private String orderId;
    /**
     * 商品SKU - activity + activity count
     */
    private Long sku;
    /**
     *  业务防重ID
     */
    private String outBusinessNo;

    /**
     * 交易类型 - 返利or支付
     */
    private SkuRechargeTypeVO skuRechargeTypeVO;
}
