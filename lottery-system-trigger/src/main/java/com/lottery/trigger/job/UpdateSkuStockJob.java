package com.lottery.trigger.job;


import com.lottery.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.lottery.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 更新sku库存的任务
 */
@Component
@Slf4j
public class UpdateSkuStockJob {

    @Autowired
    private IRaffleActivitySkuStockService activitySkuStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            log.info("定时任务，更新活动sku库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            boolean flag = true;
            while(flag){
                Long sku = activitySkuStock.takeTotalQueueValue();
                if (sku == null) {
                    return;
                }
                ActivitySkuStockKeyVO activitySkuStockKeyVO = activitySkuStock.takeQueueValue(sku);
                if (activitySkuStockKeyVO == null) {
                    continue;
                }
                log.info("定时任务，更新活动sku库存 sku:{}", activitySkuStockKeyVO.getSku());
                activitySkuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
                flag = false;
            }
        } catch (Exception e) {
            log.error("定时任务，更新活动sku库存失败", e);
        }
    }


}
