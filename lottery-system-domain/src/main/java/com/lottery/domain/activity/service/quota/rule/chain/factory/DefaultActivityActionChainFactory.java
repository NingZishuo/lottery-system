package com.lottery.domain.activity.service.quota.rule.chain.factory;

import com.lottery.domain.activity.service.quota.rule.chain.IActivityActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultActivityActionChainFactory {

    private final Map<String, IActivityActionChain> activityActionFilterMap;

    @Autowired
    public DefaultActivityActionChainFactory(Map<String, IActivityActionChain> activityActionFilterMap) {
        this.activityActionFilterMap = activityActionFilterMap;
    }

    public IActivityActionChain openActivityActionChain() {

        IActivityActionChain firstActivityActionChain = activityActionFilterMap.get(ActionModel.activity_base_action.code);

        firstActivityActionChain.appendNext(activityActionFilterMap.get(ActionModel.activity_sku_stock_action.code));

        return firstActivityActionChain;

    }




    @Getter
    @AllArgsConstructor
    public enum ActionModel{
        activity_base_action("activity_base_action","sku所属活动基础信息校验"),
        activity_sku_stock_action("activity_sku_stock_action","sku库存校验"),
        ;

        private final String code;
        private final String info;
    }

}
