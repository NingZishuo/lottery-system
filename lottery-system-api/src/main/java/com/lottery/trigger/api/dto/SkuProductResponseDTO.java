package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * sku商品对象
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductResponseDTO {
    /**
     * sku 基本信息
     */
    private ActivitySku activitySku;

    /**
     * sku充值次数
     */
    private Integer totalCount;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActivitySku {
        /**
         * 商品sku
         */
        private Long sku;
        /**
         * 活动ID
         */
        private Long activityId;
        /**
         * sku充值次数Id
         */
        private Long activityCountId;
        /**
         * 库存总量
         */
        private Integer stockCount;
        /**
         * 剩余库存
         */
        private Integer stockCountSurplus;
        /**
         * 商品金额【积分】
         */
        private BigDecimal productPrice;
    }


}
