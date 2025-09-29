package com.lottery.domain.activity.service.product;

import com.lottery.domain.activity.model.entity.RaffleActivityCountEntity;
import com.lottery.domain.activity.model.entity.RaffleActivitySkuEntity;
import com.lottery.domain.activity.model.entity.SkuProductEntity;
import com.lottery.domain.activity.repository.IActivityRepository;
import com.lottery.domain.activity.service.IRaffleActivitySkuProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * sku商品服务
 */
@Service
@Slf4j
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Autowired
    private IActivityRepository activityRepository;

    /**
     * 查询所有Sku商品
     *
     * @param activityId
     * @return
     */
    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {

        List<SkuProductEntity> skuProductEntityList = new ArrayList<>();
        List<RaffleActivitySkuEntity> raffleActivitySkuEntityList = activityRepository.queryActivitySkuList(activityId);
        if (raffleActivitySkuEntityList == null || raffleActivitySkuEntityList.isEmpty()) {
            return skuProductEntityList;
        }
        for (RaffleActivitySkuEntity raffleActivitySkuEntity : raffleActivitySkuEntityList) {
            SkuProductEntity.ActivitySku activitySku = new SkuProductEntity.ActivitySku();
            BeanUtils.copyProperties(raffleActivitySkuEntity, activitySku);
            RaffleActivityCountEntity raffleActivityCountEntity = activityRepository.queryRaffleActivityCount(raffleActivitySkuEntity.getActivityCountId());
            SkuProductEntity skuProductEntity = SkuProductEntity.builder()
                    .totalCount(raffleActivityCountEntity.getTotalCount())
                    .activitySku(activitySku)
                    .build();
            skuProductEntityList.add(skuProductEntity);
        }

        return skuProductEntityList;
    }
}
