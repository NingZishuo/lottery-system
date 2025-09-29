package com.lottery.types.common;

public class Constants {

    public final static String SPLIT = ",";
    public final static String SPACE = " ";
    public final static String COLON = ":";

    public static class RedisKey{
        public static String STRATEGY_KEY = "big_market:strategy:";
        public static String STRATEGY_AWARD_KEY = "big_market:strategy:award:";
        public static String STRATEGY_AWARD_LIST_KEY = "big_market:strategy:award:list:";
        public static String STRATEGY_AWARD_COUNT_KEY = "big_market:strategy:award:count:";
        public static String STRATEGY_RATE_TABLE_KEY = "big_market:strategy:rate:table:";
        public static String STRATEGY_RATE_RANGE_KEY = "big_market:strategy:rate:range:";
        public static String RULE_TREE_VO_KEY = "big_market:rule:tree:vo:";
        public static String STRATEGY_AWARD_BLOCK_QUEUE_KEY = "big_market:strategy:award:block_queue";

        public static String ACTIVITY_KEY = "big_market:activity:";
        public static String ACTIVITY_COUNT_KEY = "big_market:activity:count:";
        public static String ACTIVITY_SKU_KEY = "big_market:activity:sku:";
        public static String ACTIVITY_SKU_COUNT_KEY = "big_market:activity:sku:count:";
        public static String ACTIVITY_SKU_BLOCK_QUEUE_KEY = "big_market:activity:sku:block_queue:";
        public static String ACTIVITY_SKU_BLOCK_QUEUE_TOTAL_KEY = "big_market:activity:sku:block_queue:total";

        public static String USER_CREDIT_ACCOUNT_KEY = "big_market:user:credit:account:";
        public static String ACTIVITY_USER_COMPLETED_DRAW_COUNT_KEY = "big_market:activity:user:completed:draw:count:";

    }


}
