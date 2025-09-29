package com.lottery.trigger.http;

import com.alibaba.fastjson.JSON;
import com.lottery.domain.activity.service.IRaffleActivityPartakeService;
import com.lottery.domain.strategy.model.entity.RaffleAwardEntity;
import com.lottery.domain.strategy.model.entity.RaffleFactorEntity;
import com.lottery.domain.strategy.model.entity.StrategyAwardEntity;
import com.lottery.domain.strategy.service.IRaffleAward;
import com.lottery.domain.strategy.service.IRaffleRule;
import com.lottery.domain.strategy.service.IRaffleStrategy;
import com.lottery.domain.strategy.service.armory.IStrategyArmory;
import com.lottery.trigger.api.IRaffleStrategyAwardService;
import com.lottery.trigger.api.dto.*;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import com.lottery.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 仅处理抽奖策略
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy/award")
public class RaffleStrategyAwardController implements IRaffleStrategyAwardService {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IRaffleRule raffleRule;

    @Autowired
    private IRaffleActivityPartakeService activityPartakeService;

    /**
     * 策略装配接口
     *
     * @param strategyId
     * @return
     */
    @GetMapping("/strategy_armory")
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
        try {
            log.info("抽奖策略装配开始,strategy_id: {}", strategyId);
            Boolean result = strategyArmory.assembleLotteryStrategy(strategyId);
            log.error("抽奖策略装配成功,result: {}", result);
            return Response.success(result);
        } catch (Exception e) {
            log.error("抽奖策略装配失败");
            return Response.fail();
        }
    }

    /**
     * 抽奖奖品列表请求接口
     *
     * @param raffleStrategyAwardRequestDTO
     * @return
     */
    @PostMapping("/query_raffle_award_list")
    @Override
    public Response<List<RaffleStrategyAwardResponseDTO>> queryRaffleAwardList(@RequestBody RaffleStrategyAwardRequestDTO raffleStrategyAwardRequestDTO) {
        try {
            String userId = raffleStrategyAwardRequestDTO.getUserId();
            Long activityId = raffleStrategyAwardRequestDTO.getActivityId();
            log.info("查询抽奖奖品列表配开始 userId:{} activityId：{}", userId, activityId);
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || activityId == null) {
                return Response.fail(new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo()));
            }
            // 2.查询奖品配置
            List<StrategyAwardEntity> strategyAwardEntityList = raffleAward.queryRaffleStrategyAwardListByActivityId(activityId);
            if (strategyAwardEntityList == null || strategyAwardEntityList.isEmpty()) {
                return Response.fail(new AppException(ResponseCode.STRATEGY_AWARD_IS_NULL.getCode(), ResponseCode.STRATEGY_AWARD_IS_NULL.getInfo()));
            }
            // 2.1 获取策略Id
            Long strategyId = strategyAwardEntityList.get(0).getStrategyId();
            // 2.2收集awardId
            List<Long> awardIdList = strategyAwardEntityList.stream()
                    .map((Function<StrategyAwardEntity, Long>) StrategyAwardEntity::getAwardId)
                    .collect(Collectors.toList());
            // 3. 查询规则配置 - 获取奖品的解锁限制，抽奖N次后解锁
            //Long 是awardId   Integer是这个awardId对应的lock数
            Map<Long, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(strategyId, awardIdList);
            // 4. 查询抽奖次数 - 用户已经参与的抽奖次数
            Integer completedDrawCount = activityPartakeService.queryCompletedDrawCount(userId, activityId);
            // 5.遍历填充数据
            List<RaffleStrategyAwardResponseDTO> collect = strategyAwardEntityList.stream().map(strategyAwardEntity -> {
                RaffleStrategyAwardResponseDTO raffleStrategyAwardResponseDTO = new RaffleStrategyAwardResponseDTO();
                BeanUtils.copyProperties(strategyAwardEntity, raffleStrategyAwardResponseDTO);
                //5.1获取奖品所需解锁次数
                Integer ruleLockCount = ruleLockCountMap.get(strategyAwardEntity.getAwardId());
                if (ruleLockCount != null) {
                    raffleStrategyAwardResponseDTO.setAwardRuleLockCount(ruleLockCount);
                    raffleStrategyAwardResponseDTO.setIsAwardUnLock(completedDrawCount >= ruleLockCount);
                    raffleStrategyAwardResponseDTO.setWaitUnLockCount(Math.max(ruleLockCount - completedDrawCount, 0));
                }
                return raffleStrategyAwardResponseDTO;
            }).collect(Collectors.toList());
            return Response.success(collect);
        } catch (Exception e) {
            log.error("查询抽奖奖品列表失败",e);
            return Response.fail();
        }
    }

    /**
     * 抽奖请求接口 返回奖品id和index值
     *
     * @param raffleStrategyRequestDTO
     * @return
     */
    //@PostMapping("/random_raffle")
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO raffleStrategyRequestDTO) {
        try {
            log.info("随机抽奖开始 strategyId: {} userId:{}", raffleStrategyRequestDTO.getStrategyId(), raffleStrategyRequestDTO.getUserId());
            // 调用抽奖接口
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .strategyId(raffleStrategyRequestDTO.getStrategyId())
                    .userId(raffleStrategyRequestDTO.getUserId())
                    .build());
            log.info("随机抽奖完成");
            return Response.success(RaffleStrategyResponseDTO.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardIndex(raffleAwardEntity.getSort())
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    .build());
        } catch (AppException e) {
            //只有业务需要给前端看啊
            log.error("随机抽奖失败");
            return Response.fail(e);
        } catch (Exception e) {
            //后端咋样无所谓的
            log.error("随机抽奖失败", e);
            return Response.fail();
        }
    }


    /**
     * 查询1.用户在该活动下完成的已抽奖次数 2.权重及其对应可抽奖范围
     */
    @PostMapping("/query_total_used_count_and_raffle_strategy_award_and_rule_weight")
    @Override
    public Response<RaffleStrategyRuleWeightResponseDTO> queryRaffleStrategyRuleWeight(@RequestBody RaffleStrategyRuleWeightRequestDTO raffleStrategyRuleWeightRequestDTO) {
        String userId = raffleStrategyRuleWeightRequestDTO.getUserId();
        Long activityId = raffleStrategyRuleWeightRequestDTO.getActivityId();
        try {
            log.info("查询抽奖策略权重规则配置开始 userId:{} activityId：{}", userId, activityId);
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.获取用户在该活动的已抽奖次数
            Integer totalUsedCount = activityPartakeService.queryCompletedDrawCount(userId, activityId);

            //3.获取所有权重及其对应的抽奖范围
            Map<String, List<StrategyAwardEntity>> map = raffleRule.queryRaffleStrategyRuleWeightAndAwards(activityId);

            Map<String, List<RaffleStrategyRuleWeightResponseDTO.StrategyAward>> ruleWeightAndStrategyAwardList = new LinkedHashMap<>();
            for (String ruleWeight : map.keySet()) {
                List<StrategyAwardEntity> strategyAwardEntityList = map.get(ruleWeight);
                List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = strategyAwardEntityList.stream()
                        .map(strategyAwardEntity -> RaffleStrategyRuleWeightResponseDTO.StrategyAward.builder()
                                .awardId(strategyAwardEntity.getAwardId())
                                .awardTitle(strategyAwardEntity.getAwardTitle())
                                .build())
                        .collect(Collectors.toList());
                ruleWeightAndStrategyAwardList.put(ruleWeight, strategyAwards);
            }

            log.info("查询抽奖策略权重规则配置完成 userId:{} activityId：{}totalUsedCount: {},ruleWeightAndStrategyAwardList:{}", userId, activityId, totalUsedCount, JSON.toJSONString(ruleWeightAndStrategyAwardList));
            return Response.success(RaffleStrategyRuleWeightResponseDTO.builder()
                    .userActivityAccountTotalUsedCount(totalUsedCount)
                    .ruleWeightAndStrategyAwardList(ruleWeightAndStrategyAwardList)
                    .build());
        } catch (Exception e) {
            log.error("查询抽奖策略权重规则配置失败 userId:{} activityId：{}", userId, activityId, e);
            return Response.fail();
        }

    }
}
