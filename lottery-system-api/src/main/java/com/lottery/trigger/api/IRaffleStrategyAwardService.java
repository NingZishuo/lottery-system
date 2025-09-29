package com.lottery.trigger.api;

import com.lottery.trigger.api.dto.*;
import com.lottery.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口 单纯抽取策略奖励的接口
 * 包含对抽奖策略和抽取策略奖励两部分
 */
public interface IRaffleStrategyAwardService {

    /**
     * 策略装配接口
     * @param strategyId
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);


    /**
     * 抽奖奖品请求接口
     * @param raffleStrategyAwardRequestDTO
     * @return
     */
    Response<List<RaffleStrategyAwardResponseDTO>> queryRaffleAwardList(RaffleStrategyAwardRequestDTO raffleStrategyAwardRequestDTO);

    /**
     * 查询1.用户在该活动下完成的已抽奖次数 2.权重及其对应可抽奖范围
     * @return
     */
    Response<RaffleStrategyRuleWeightResponseDTO> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO raffleStrategyRuleWeightRequestDTO);



    /**
     * 抽奖请求接口 返回奖品id和index值
     * @param raffleStrategyRequestDTO
     * @return
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO raffleStrategyRequestDTO);



}
