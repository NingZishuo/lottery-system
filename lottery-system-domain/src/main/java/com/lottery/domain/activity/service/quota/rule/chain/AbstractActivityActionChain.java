package com.lottery.domain.activity.service.quota.rule.chain;

public abstract class AbstractActivityActionChain implements IActivityActionChain {

    private IActivityActionChain nextChain;


    @Override
    public IActivityActionChain next() {
        return this.nextChain;
    }

    @Override
    public IActivityActionChain appendNext(IActivityActionChain nextChain) {
        this.nextChain = nextChain;
        return nextChain;
    }
}
