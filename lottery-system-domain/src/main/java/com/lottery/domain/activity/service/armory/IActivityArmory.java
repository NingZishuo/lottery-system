package com.lottery.domain.activity.service.armory;

/**
 * 活动装配接口
 */
public interface IActivityArmory {


   /**
    * 活动装配
    * @param sku
    * @return
    */
   boolean assembleActivitySku(Long sku);


   /**
    * 装配活动及其对应Sku
    * @param activityId
    * @return
    */
   boolean assembleActivityAndSkuByActivityId(Long activityId);



}
