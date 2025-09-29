package com.lottery.domain.activity.service.quota.rule.chain;


/**
 * 装配接口
 * 这样当你调用ILogicChain的方法的时候 只有ILogicChain的方法会被加粗 虽然appendNext和next也能调用 但是是细小字体
 */
public interface IActivityActionChainArmory {

    IActivityActionChain appendNext(IActivityActionChain next);

    IActivityActionChain next();


}
