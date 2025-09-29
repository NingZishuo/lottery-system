package com.lottery.domain.activity.service;

import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;

/**
 * sku库存处理接口
 */
public interface IRaffleActivitySkuStockService {

    /**
     * 获取skuTotal库存消耗队列
     * @return
     * @throws InterruptedException
     */
    Long takeTotalQueueValue()throws InterruptedException;

    /**
     * 获取sku库存消耗队列
     * @return
     * @throws InterruptedException
     */
    ActivitySkuStockKeyVO takeQueueValue(Long sku)throws InterruptedException;

    /**
     * 清空队列 要根据sku清空队列  即每个sku库存分别用一个队列
     */
    void clearQueueValue(Long sku);

    /**
     * 延迟队列 + 任务趋势更新活动sku库存
     * @param sku
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存消耗完毕,清空数据库库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
}
