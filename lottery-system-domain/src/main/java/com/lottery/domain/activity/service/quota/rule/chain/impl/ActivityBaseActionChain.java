package com.lottery.domain.activity.service.quota.rule.chain.impl;

import com.lottery.domain.activity.model.entity.RaffleActivityCountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivityEntity;
import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;
import com.lottery.domain.activity.model.valobj.ActivityStateVO;
import com.lottery.domain.activity.service.quota.rule.chain.AbstractActivityActionChain;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component("activity_base_action")
@Slf4j
public class ActivityBaseActionChain extends AbstractActivityActionChain {
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
        log.info("活动责任链-基础信息【有效期、状态、库存(sku)】校验开始。sku:{} activityId:{}", raffleActivitySkuEntity.getSku(), raffleActivityEntity.getActivityId());
        // 校验；活动状态
        if (!ActivityStateVO.open.equals(raffleActivityEntity.getState())) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        // 校验；活动日期「开始时间 <- 当前时间 -> 结束时间」
        LocalDateTime now = LocalDateTime.now();
        if (raffleActivityEntity.getEndDateTime().isBefore(now) || raffleActivityEntity.getBeginDateTime().isAfter(now)) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        // 校验；活动sku库存 「剩余库存从缓存获取的」 老方法:以redis的库存为准 所以这里不需要sku数据库的库存
        /*if (raffleActivitySkuEntity.getStockCountSurplus() <= 0) {
            throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }*/

        log.info("活动责任链-基础信息 放行");
        return next().action(raffleActivitySkuEntity, raffleActivityEntity, raffleActivityCountEntity);
    }


}
