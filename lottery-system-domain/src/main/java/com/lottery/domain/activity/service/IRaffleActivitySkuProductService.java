package com.lottery.domain.activity.service;

import com.lottery.domain.activity.model.entity.SkuProductEntity;

import java.util.List;

/**
 * sku商品服务
 */
public interface IRaffleActivitySkuProductService {

    /**
     * 查询所有Sku商品
     *
     * @param activityId
     * @return
     */
    List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId);

}
