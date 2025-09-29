package com.lottery.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    INDEX_DUP("0003", "唯一索引冲突"),
    STRATEGY_IS_NULL("ERR_BIZ_000", "不存在该strategy"),
    STRATEGY_RULE_WEIGHT_IS_NULL("ERR_BIZ_001", "无法查询到具体rule_model"),
    STRATEGY_RULE_WEIGHT_FORMAT_IS_WRONG("ERR_BIZ_002", "rule_model格式有误,解析失败"),
    STRATEGY_BLACK_LISTED_USER_IS_NULL("ERR_BIZ_003", "不存在该黑名单用户"),
    AWARD_IS_NULL("ERR_BIZ_004", "不存在该award"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_005", "抽奖策略配置未装配，请通过IStrategyArmory完成装配"),
    SKU_IS_NULL("ERR_BIZ_006", "sku不存在"),
    ACTIVITY_IS_NULL("ERR_BIZ_007", "无法查询sku,对应所属的活动"),
    ACTIVITY_COUNT_IS_NULL("ERR_BIZ_008", "没有给sku设置活动参与上限"),
    ACTIVITY_STATE_ERROR("ERR_BIZ_009", "活动未开启（非open状态）"),
    ACTIVITY_DATE_ERROR("ERR_BIZ_010", "非活动日期范围"),
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_011", "活动库存不足"),
    ACCOUNT_QUOTA_ERROR("ERR_BIZ_012","账户总额度不足"),
    ACCOUNT_MONTH_QUOTA_ERROR("ERR_BIZ_013","账户月额度不足"),
    ACCOUNT_DAY_QUOTA_ERROR("ERR_BIZ_014","账户日额度不足"),
    USER_RAFFLE_ORDER_ERROR("ERR_BIZ_015","抽奖单异常,被使用过"),
    STRATEGY_AWARD_IS_NULL("ERR_BIZ_016","该策略不存在任何策略奖励"),
    DAILY_BEHAVIOR_REBATE_IS_NULL("ERR_BIZ_017","不存在该日常返利行为"),
    UNIQUE_INDEX_REPEATED("ERR_BIZ_018","唯一索引出现异常"),
    REPEAT_DISTRIBUTE_AWARD("ERR_BIZ_019","重复发奖"),
    REPEAT_DISTRIBUTE_SKU("ERR_BIZ_020","重复发放SKU或SKU不存在"),
    TRADE_POLICY_IS_NULL("ERR_BIZ_021","不存在对应策略"),
    CREDIT_IS_NOT_ENOUGH("ERR_BIZ_022","积分不足,无法支付sku"),
    ACTIVITY_ORDER_IS_ENOUGH("ERR_BIZ_023","sku支付订单为null,无法支付该订单")
    ;

    private String code;
    private String info;

}
