package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品sku dao
 */
@Mapper
public interface IRaffleActivitySkuDao {

    /**
     * 综合查询
     * @param raffleActivitySku
     * @return
     */
    RaffleActivitySku queryRaffleActivitySku(RaffleActivitySku raffleActivitySku);

    /**
     * 综合查询
     * @param raffleActivitySku
     * @return
     */
    List<RaffleActivitySku> queryRaffleActivitySkuList(RaffleActivitySku raffleActivitySku);

    /**
     * 更新sku库存
     * @param sku
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 清空sku库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
}
