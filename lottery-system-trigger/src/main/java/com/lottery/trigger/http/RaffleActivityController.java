package com.lottery.trigger.http;

import com.alibaba.fastjson.JSON;
import com.lottery.domain.activity.model.entity.*;
import com.lottery.domain.activity.model.valobj.RaffleTypeVO;
import com.lottery.domain.activity.model.valobj.SkuRechargeTypeVO;
import com.lottery.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.lottery.domain.activity.service.IRaffleActivityPartakeService;
import com.lottery.domain.activity.service.IRaffleActivitySkuProductService;
import com.lottery.domain.activity.service.armory.IActivityArmory;
import com.lottery.domain.award.model.entity.ShowAwardRecordEntity;
import com.lottery.domain.award.model.entity.UserAwardRecordEntity;
import com.lottery.domain.award.model.valobj.AwardStateVO;
import com.lottery.domain.award.service.IAwardService;
import com.lottery.domain.credit.model.entity.TradeEntity;
import com.lottery.domain.credit.model.valobj.OrderTradeTypeVO;
import com.lottery.domain.credit.model.valobj.TradeNameVO;
import com.lottery.domain.credit.model.valobj.TradeTypeVO;
import com.lottery.domain.credit.service.ICreditAdjustService;
import com.lottery.domain.rebate.model.entity.BehaviorEntity;
import com.lottery.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.lottery.domain.rebate.model.valobj.BehaviorTypeVO;
import com.lottery.domain.rebate.service.IBehaviorRebateService;
import com.lottery.domain.strategy.model.entity.RaffleAwardEntity;
import com.lottery.domain.strategy.model.entity.RaffleFactorEntity;
import com.lottery.domain.strategy.service.IRaffleStrategy;
import com.lottery.domain.strategy.service.armory.IStrategyArmory;
import com.lottery.trigger.api.IRaffleActivityService;
import com.lottery.trigger.api.dto.*;
import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import com.lottery.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 抽奖活动服务 注意；在不引用 application/case 层的时候，就需要让接口实现层来做领域的串联。一些较大规模的系统，需要加入 case 层 注意这里会同时处理抽奖策略那块内容 就不只是抽奖活动
 */
@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
public class RaffleActivityController implements IRaffleActivityService {

    @Autowired
    private IActivityArmory activityArmory;

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Autowired
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IAwardService awardService;

    @Autowired
    private IBehaviorRebateService behaviorRebateService;

    @Autowired
    private ICreditAdjustService creditAdjustService;

    @Autowired
    private IRaffleActivitySkuProductService raffleActivitySkuProductService;

    private ExecutorService executorService = Executors.newFixedThreadPool(50);

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * 入参：{"activityId":100001,"userId":"zishuoning"}
     * <p>
     * curl --request GET \
     * --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */

    @GetMapping("/armory")
    @Override
    public Response<Boolean> armory(@RequestParam Long activityId) {

        try {
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            // 1. 活动装配
            activityArmory.assembleActivityAndSkuByActivityId(activityId);
            // 2. 策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return Response.success(true);
        } catch (Exception e) {
            log.error("活动装配,策略装配失败 activityId={}", activityId, e);
            return Response.fail();
        }
    }

    /**
     * 抽奖接口
     *
     * @param raffleActivityDrawRequestDTO 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"zishuoning"}
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/draw \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"zishuoning",
     * "activityId": 101
     * }'
     */
    @PostMapping("/draw")
    @Override
    public Response<RaffleActivityDrawResponseDTO> draw(@RequestBody RaffleActivityDrawRequestDTO raffleActivityDrawRequestDTO) {
        String userId = raffleActivityDrawRequestDTO.getUserId();
        Long activityId = raffleActivityDrawRequestDTO.getActivityId();
        try {
            log.info("活动单抽奖 userId:{} activityId:{}", userId, activityId);
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || activityId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 参与活动 - 创建参与记录订单
            UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartakeService.createPartakeOrder(userId, activityId, RaffleTypeVO.single);
            log.info("活动单抽奖，创建订单 userId:{} activityId:{} orderId:{}", userId, activityId, userRaffleOrderEntity.getOrderId());
            //TODO 高并发问题
            // 3. 抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(userId)
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .build());
            // 4. 存放结果 - 写入中奖记录
            UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    //哪个中奖单让你中的这个奖品
                    .orderId(userRaffleOrderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    //中奖时间
                    .awardTime(LocalDateTime.now())
                    //只有等发奖完成 这里才能是 complete("complete", "发奖完成")
                    .awardState(AwardStateVO.create)
                    .build();
            //1.插入中奖记录 2.报废抽奖单
            //注意 这里使用线程池  假如返回抽奖结果 但是线程失败的话
            //中奖记录无 没有task进行发奖  抽奖单未被使用  所以用户直接再次点击抽奖就好了 无伤大雅
            awardService.saveUserAwardRecord(userAwardRecord);
            // 5. 返回结果 告诉前端这个用户中了啥
            return Response.success(RaffleActivityDrawResponseDTO.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardConfig(raffleAwardEntity.getAwardConfig())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardIndex(raffleAwardEntity.getSort())
                    .build());
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail(e);
        } catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail();
        }

    }


    /**
     * 十连抽奖接口
     *
     * @param raffleActivityDrawRequestDTO 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"zishuoning"}
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/draw \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"zishuoning",
     * "activityId": 101
     * }'
     */
    @PostMapping("/drawTen")
    @Override
    public Response<List<RaffleActivityDrawResponseDTO>> drawTen(@RequestBody RaffleActivityDrawRequestDTO raffleActivityDrawRequestDTO) {
        String userId = raffleActivityDrawRequestDTO.getUserId();
        Long activityId = raffleActivityDrawRequestDTO.getActivityId();
        try {
            log.info("活动十连抽奖 userId:{} activityId:{}", userId, activityId);
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || activityId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 参与活动 - 创建参与记录订单
            UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartakeService.createPartakeOrder(userId, activityId, RaffleTypeVO.ten);
            log.info("活动十连抽奖，创建订单 userId:{} activityId:{} orderId:{}", userId, activityId, userRaffleOrderEntity.getOrderId());
            List<Callable<UserAwardRecordEntity>> tasks = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (int i = 0; i < RaffleTypeVO.ten.getSubtractor(); i++) {
                tasks.add(() -> {
                    // 3. 抽奖策略 - 执行抽奖
                    RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                            .userId(userId)
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .build());

                    // 任务只返回中奖记录，不操作共享列表
                    UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                            .userId(userId)
                            .activityId(userRaffleOrderEntity.getActivityId())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardConfig(raffleAwardEntity.getAwardConfig())
                            .awardTime(now)
                            .awardState(AwardStateVO.create)
                            .sort(raffleAwardEntity.getSort())
                            .build();
                    return userAwardRecord;
                });
            }

            // 5. 使用 invokeAll 批量执行所有任务
            List<Future<UserAwardRecordEntity>> futures = executorService.invokeAll(tasks);

            // 6. 存放最终中奖记录的列表
            List<UserAwardRecordEntity> userAwardRecords = new ArrayList<>();
            // 7. 存放最终返回给前端的DTO列表
            List<RaffleActivityDrawResponseDTO> responseDTOs = new ArrayList<>();
            // 8. 在主线程中安全地遍历 Future 列表，构建两个结果列表
            for (Future<UserAwardRecordEntity> future : futures) {
                UserAwardRecordEntity userAwardRecord = future.get();
                userAwardRecords.add(userAwardRecord);

                // 从中奖记录中获取信息，构建DTO
                responseDTOs.add(RaffleActivityDrawResponseDTO.builder()
                        .awardId(userAwardRecord.getAwardId())
                        .awardConfig(userAwardRecord.getAwardConfig())
                        .awardTitle(userAwardRecord.getAwardTitle())
                        .awardIndex(userAwardRecord.getSort())
                        .build());
            }
            awardService.saveUserAwardRecordList(userAwardRecords);
            return Response.success(responseDTOs);
        } catch (AppException e) {
            log.error("活动十连抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail(e);
        } catch (Exception e) {
            log.error("活动十连抽奖失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail();
        }

    }


    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到返利结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate">/api/v1/raffle/activity/calendar_sign_rebate</a>
     * 入参：zishuoning
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate -d "userId=zishuoning" -H "Content-Type: application/x-www-form-urlencoded"
     */

    @PostMapping("/calendar_sign_rebate")
    @Override
    public Response<Boolean> calendarSignRebate(@RequestParam String userId) {
        try {
            log.info("日历签到返利开始 userId:{}", userId);
            List<String> orderIds = behaviorRebateService.createBehaviorRebateOrder(BehaviorEntity.builder()
                    .userId(userId)
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .build());
            log.info("日历签到返利完成 userId:{} orderIds: {}", userId, JSON.toJSONString(orderIds));
            return Response.success(true);
        } catch (AppException e) {
            log.error("日历签到返利异常 userId:{} ", userId, e);
            return Response.fail(e);
        } catch (Exception e) {
            log.error("日历签到返利失败 userId:{}", userId, e);
            return Response.fail(false);
        }
    }


    /**
     * 判断是否签到接口
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/is_calendar_sign_rebate -d "userId=zishuoning" -H "Content-Type: application/x-www-form-urlencoded"
     */
    @PostMapping("/is_calendar_sign_rebate")
    @Override
    public Response<Boolean> isCalendarSignRebate(@RequestParam String userId) {
        try {
            log.info("查询用户是否完成日历签到返利开始 userId:{}", userId);
            List<BehaviorRebateOrderEntity> behaviorRebateOrderEntities = behaviorRebateService.queryUserRebateOrderByBehaviorType(BehaviorEntity.builder()
                    .userId(userId)
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .build());
            return Response.success(!behaviorRebateOrderEntities.isEmpty());
        } catch (Exception e) {
            log.error("查询用户是否完成日历签到返利失败 userId:{}", userId, e);
            return Response.fail(false);
        }
    }

    /**
     * 查询账户额度
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/query_user_activity_account \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"",
     * "activityId": 101
     * }'
     */
    @PostMapping("/query_user_activity_account")
    @Override
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(@RequestBody UserActivityAccountRequestDTO userActivityAccountRequestDTO) {

        String userId = userActivityAccountRequestDTO.getUserId();
        Long activityId = userActivityAccountRequestDTO.getActivityId();
        try {
            log.info("查询用户活动账户开始 userId:{} activityId:{}", userId, activityId);
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || activityId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            RaffleActivityAccountEntity raffleActivityAccountEntity = raffleActivityAccountQuotaService.queryActivityAccount(userId, activityId);
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = new UserActivityAccountResponseDTO();
            BeanUtils.copyProperties(raffleActivityAccountEntity, userActivityAccountResponseDTO);
            log.info("查询用户活动账户完成 userId:{} activityId:{} dto:{}", userId, activityId, JSON.toJSONString(userActivityAccountResponseDTO));
            return Response.success(userActivityAccountResponseDTO);
        } catch (Exception e) {
            log.error("查询用户活动账户失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail();
        }

    }

    /**
     * 积分购买sku充值次数
     *
     * @param skuProductCreateOrderDTO
     */
    @PostMapping("/credit_pay_exchange_sku_recharge_create_order")
    @Override
    public Response<Boolean> creditPayExchangeSkuRechargeCreateOrder(@RequestBody SkuProductCreateOrderDTO skuProductCreateOrderDTO) {
        String userId = skuProductCreateOrderDTO.getUserId();
        Long sku = skuProductCreateOrderDTO.getSku();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String outBusinessNo = userId + "_" + "creditPay-" + sku + "_" + formatNow;
        try {
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || sku == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            log.info("积分兑换sku创建订单开始 userId:{} sku:{}", userId, sku);

            raffleActivityAccountQuotaService.createSkuRechargeOrder(SkuRechargeEntity.builder()
                    .userId(userId)
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .sku(sku)
                    .outBusinessNo(outBusinessNo)
                    .skuRechargeTypeVO(SkuRechargeTypeVO.credit_pay_trade_order_create)
                    .build());

            log.info("积分兑换sku创建订单完成 userId:{} sku:{} outBusinessNo:{}", userId, sku, outBusinessNo);

            return Response.success(true);

        } catch (Exception e) {
            log.error("积分兑换sku创建订单失败 userId:{} sku:{}", userId, sku, e);
            return Response.fail(false);
        }

    }

    /**
     * 积分购买sku充值次数 完成订单
     *
     * @param skuProductCompleteOrderRequestDTO
     */
    @PostMapping("/credit_pay_exchange_sku_recharge_complete_order")
    @Override
    public Response<Boolean> creditPayExchangeSkuRechargeCompleteOrder(@RequestBody SkuProductCompleteOrderRequestDTO skuProductCompleteOrderRequestDTO) {
        String userId = skuProductCompleteOrderRequestDTO.getUserId();
        String orderId = skuProductCompleteOrderRequestDTO.getOrderId();
        String outBusinessNo = skuProductCompleteOrderRequestDTO.getOutBusinessNo();
        try {
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId) || StringUtils.isBlank(outBusinessNo)) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            log.info("积分兑换sku完成订单开始,扣减积分 发送mq增加次数 userId:{} orderId:{} outBusinessNo:{}", userId, orderId, outBusinessNo);

            //2.扣减积分 发送mq增加账户次数
            creditAdjustService.createCreditOrder(TradeEntity.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .tradeName(TradeNameVO.CONVERT_SKU)
                    .tradeType(TradeTypeVO.REVERSE)
                    .outBusinessNo(outBusinessNo)
                    .orderTradeType(OrderTradeTypeVO.credit_pay_trade_order_complete)
                    .build());
            log.info("积分兑换sku完成订单开始,扣减积分 发送mq增加次数完成 userId:{} orderId:{} outBusinessNo:{}", userId, orderId, outBusinessNo);
            return Response.success(true);
        } catch (Exception e) {
            log.error("积分兑换sku完成订单失败,扣减积分 发送mq增加次数失败 userId:{} orderId:{} outBusinessNo:{}", userId, orderId, outBusinessNo, e);
            return Response.fail(false);
        }

    }


    /**
     * 积分购买sku充值次数 创建和完成订单
     *
     * @param skuProductCreateOrderDTO
     */
    @PostMapping("/credit_pay_exchange_sku")
    @Override
    public Response<Boolean> creditPayExchangeSkuRecharge(@RequestBody SkuProductCreateOrderDTO skuProductCreateOrderDTO) {
        String userId = skuProductCreateOrderDTO.getUserId();
        Long sku = skuProductCreateOrderDTO.getSku();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        String formatNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String outBusinessNo = userId + "_" + "creditPay-" + sku + "_" + formatNow;
        try {
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || sku == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            log.info("积分兑换sku开始 userId:{} sku:{}", userId, sku);
            //2. 创建活动积分订单
            RaffleActivityOrderEntity raffleActivityOrderEntity = raffleActivityAccountQuotaService.createSkuRechargeOrder(SkuRechargeEntity.builder()
                    .userId(userId)
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .sku(sku)
                    .outBusinessNo(outBusinessNo)
                    .skuRechargeTypeVO(SkuRechargeTypeVO.credit_pay_trade_order_create)
                    .build());

            //3.扣减积分 发送mq增加账户次数
            creditAdjustService.createCreditOrder(TradeEntity.builder()
                    .userId(userId)
                    .orderId(raffleActivityOrderEntity.getOrderId())
                    .tradeName(TradeNameVO.CONVERT_SKU)
                    .tradeType(TradeTypeVO.REVERSE)
                    .outBusinessNo(outBusinessNo)
                    .orderTradeType(OrderTradeTypeVO.credit_pay_trade_order_complete)
                    .build());
            log.info("积分兑换sku完成, userId:{} outBusinessNo:{}", userId, outBusinessNo);
            return Response.success(true);
        } catch (Exception e) {
            log.error("积分兑换sku失败 userId:{} sku:{}", userId, sku, e);
            return Response.fail(false);
        }
    }

    /**
     * 查询用户有多少积分
     *
     * @param userId
     */
    @PostMapping("/query_user_credit_account_available_amount")
    @Override
    public Response<BigDecimal> queryUserCreditAccountAvailableAmount(@RequestParam String userId) {
        try {
            log.info("查询用户积分值开始 userId:{}", userId);
            BigDecimal availableAmount = creditAdjustService.queryCreditAccountAvailableAmount(userId);
            log.info("查询用户积分值完成 userId:{} availableAmount:{}", userId, availableAmount);
            return Response.success(availableAmount);
        } catch (Exception e) {
            log.error("查询用户积分值失败 userId:{}", userId, e);
            return Response.fail();
        }
    }

    /**
     * 查询活动的所有sku
     *
     * @param activityId
     * @return
     */
    @PostMapping("/query_sku_product_list_by_activity_id")
    @Override
    public Response<List<SkuProductResponseDTO>> querySkuProductListByActivityId(Long activityId) {
        try {
            log.info("查询sku商品集合开始 activityId:{}", activityId);
            // 1. 参数校验
            if (null == activityId) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 查询sku封装商品
            List<SkuProductEntity> skuProductEntities = raffleActivitySkuProductService.querySkuProductEntityListByActivityId(activityId);
            List<SkuProductResponseDTO> skuProductResponseDTOS = new ArrayList<>(skuProductEntities.size());
            for (SkuProductEntity skuProductEntity : skuProductEntities) {
                SkuProductResponseDTO.ActivitySku activitySku = new SkuProductResponseDTO.ActivitySku();
                BeanUtils.copyProperties(skuProductEntity.getActivitySku(), activitySku);
                SkuProductResponseDTO skuProductResponseDTO = SkuProductResponseDTO.builder()
                        .totalCount(skuProductEntity.getTotalCount())
                        .activitySku(activitySku)
                        .build();
                skuProductResponseDTOS.add(skuProductResponseDTO);
            }
            log.info("查询sku商品集合完成 activityId:{} skuProductResponseDTOS:{}", activityId, JSON.toJSONString(skuProductResponseDTOS));
            return Response.success(skuProductResponseDTOS);
        } catch (Exception e) {
            log.error("查询sku商品集合失败 activityId:{}", activityId, e);
            return Response.fail();
        }

    }

    /**
     * 显示用户中奖记录 只显示最近的30条
     *
     * @param userAwardRecordRequestDTO
     */
    @PostMapping("/show_user_award_record")
    @Override
    public Response<List<UserAwardRecordResponseDTO>> showUserAwardRecord(@RequestBody UserAwardRecordRequestDTO userAwardRecordRequestDTO) {
        String userId = userAwardRecordRequestDTO.getUserId();
        Long activityId = userAwardRecordRequestDTO.getActivityId();
        try {
            // 1. 参数校验
            if (StringUtils.isBlank(userId) || activityId == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            log.info("显示用户中奖记录开始 userId:{} activityId:{}", userId, activityId);
            //2.查询用户中奖记录
            List<UserAwardRecordEntity> userAwardRecordEntityList = awardService.showUserAwardRecordList(ShowAwardRecordEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .build());

            List<UserAwardRecordResponseDTO> userAwardRecordResponseDTOList = new ArrayList<>(userAwardRecordEntityList.size());
            for (UserAwardRecordEntity userAwardRecordEntity : userAwardRecordEntityList) {
                UserAwardRecordResponseDTO userAwardRecordResponseDTO = new UserAwardRecordResponseDTO();
                BeanUtils.copyProperties(userAwardRecordEntity, userAwardRecordResponseDTO);
                userAwardRecordResponseDTO.setAwardState(userAwardRecordEntity.getAwardState().getCode());
                userAwardRecordResponseDTOList.add(userAwardRecordResponseDTO);
            }
            log.info("显示用户中奖记录完成, userId:{} activityId:{} userAwardRecordResponseDTOList:{}", userId, activityId,userAwardRecordResponseDTOList);
            return Response.success(userAwardRecordResponseDTOList);
        } catch (Exception e) {
            log.error("显示用户中奖记录失败 userId:{} activityId:{}", userId, activityId, e);
            return Response.fail();
        }
    }
}
