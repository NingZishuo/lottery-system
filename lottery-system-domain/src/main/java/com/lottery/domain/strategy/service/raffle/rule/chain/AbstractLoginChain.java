package com.lottery.domain.strategy.service.raffle.rule.chain;

public abstract class AbstractLoginChain implements ILogicChain{

    private ILogicChain nextChain;

    @Override
    public ILogicChain appendNext(ILogicChain nextChain) {
        this.nextChain = nextChain;
        //返回下一个节点
        return nextChain;
    }


    @Override
    public ILogicChain next() {
        return this.nextChain;
    }


    protected abstract String ruleModel();

}
