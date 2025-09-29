package com.lottery.domain.activity.service.quota.rule.chain.impl;

import com.lottery.domain.activity.model.entity.RaffleActivityCountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityEntity;
import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;
import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.armory.IActivityDispatch;
import com.lottery.domain.activity.service.quota.rule.chain.AbstractActivityActionChain;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("activity_sku_stock_action")
@Slf4j
public class ActivitySkuStockActionChain extends AbstractActivityActionChain {

    @Resource
    private IActivityDispatch activityDispatch;

    @Resource
    private IActivityRepository activityRepository;

    /**
     * 活动责任链接口
     *
     * @param raffleActivitySkuEntity
     * @param raffleActivityEntity
     * @param raffleActivityCountEntity
     * @return
     */
    @Override
    public boolean action(RaffleActivitySkuEntity raffleActivitySkuEntity, RaffleActivityEntity raffleActivityEntity, RaffleActivityCountEntity raffleActivityCountEntity) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】开始。sku:{} activityId:{}", raffleActivitySkuEntity.getSku(), raffleActivityEntity.getActivityId());

        //1.扣减库存
        boolean status = activityDispatch.subtractionActivitySkuStock(raffleActivityEntity.getActivityId(), raffleActivitySkuEntity.getSku(), raffleActivityEntity.getEndDateTime());

        //2.库存扣减失败
        if (!status) {
            log.info("活动责任链-活动库存不足");
            throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }
        //3.库存扣减成功
        log.info("活动责任链-活动库存扣减成功");
        //4.使用延迟队列
        activityRepository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                .sku(raffleActivitySkuEntity.getSku())
                .build());;

        return true;
    }
}
