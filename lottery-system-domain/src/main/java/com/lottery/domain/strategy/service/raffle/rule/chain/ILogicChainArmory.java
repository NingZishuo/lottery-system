package com.lottery.domain.strategy.service.raffle.rule.chain;


/**
 * 装配接口
 * 这样当你调用ILogicChain的方法的时候 只有ILogicChain的方法会被加粗 虽然appendNext和next也能调用 但是是细小字体
 */
public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();


}
