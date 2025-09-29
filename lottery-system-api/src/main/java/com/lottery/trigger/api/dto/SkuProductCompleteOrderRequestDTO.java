package com.lottery.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuProductCompleteOrderRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 防重ID
     */
    private String outBusinessNo;
    /**
     * 订单ID
     */
    private String orderId;
}
