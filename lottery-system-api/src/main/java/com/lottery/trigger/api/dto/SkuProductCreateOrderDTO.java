package com.lottery.trigger.api.dto;

import lombok.Data;

/**
 *  商品购物车请求对象
 */
@Data
public class SkuProductCreateOrderDTO {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * sku 商品
     */
    private Long sku;

}
