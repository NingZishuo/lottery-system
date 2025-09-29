package com.lottery.trigger.api;

import com.lottery.trigger.api.dto.*;
import com.lottery.types.model.Response;

import java.math.BigDecimal;
import java.util.List;

/**
 * 抽奖活动服务接口
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     *
     * @param activityId 活动ID
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     *
     * @param request 请求对象
     * @return 返回结果
     */
    Response<RaffleActivityDrawResponseDTO> draw(RaffleActivityDrawRequestDTO request);


    /**
     * 活动抽奖接口
     *
     * @param request 请求对象
     * @return 返回结果
     */
    Response<List<RaffleActivityDrawResponseDTO>> drawTen(RaffleActivityDrawRequestDTO request);


    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 判断是否已经进行签到了
     *
     * @param userId
     * @return
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询用户在某个活动的总抽奖次数和剩余抽奖次数
     *
     * @return
     */
    Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO userActivityAccountRequestDTO);

    /**
     * 积分购买sku充值次数 创建订单
     */
    Response<Boolean> creditPayExchangeSkuRechargeCreateOrder(SkuProductCreateOrderDTO skuProductCreateOrderDTO);

    /**
     * 积分购买sku充值次数 完成订单
     */
    Response<Boolean> creditPayExchangeSkuRechargeCompleteOrder(SkuProductCompleteOrderRequestDTO skuProductCompleteOrderRequestDTO);

    /**
     * 积分购买sku充值次数 创建和完成订单
     */
    Response<Boolean> creditPayExchangeSkuRecharge(SkuProductCreateOrderDTO skuProductCreateOrderDTO);

    /**
     * 查询用户有多少剩余积分
     */
    Response<BigDecimal> queryUserCreditAccountAvailableAmount(String userId);

    /**
     * 查询活动的sku
     *
     * @param activityId
     * @return
     */
    Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId);


    /**
     * 显示用户中奖记录 只显示最近的30条
     */
    Response<List<UserAwardRecordResponseDTO>> showUserAwardRecord(UserAwardRecordRequestDTO userAwardRecordRequestDTO);

}
