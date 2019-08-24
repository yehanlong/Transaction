package com.transaction.core.strategy;

import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;

public class Moving implements Strategy{
    @Override
    public void start(Lock lock, Exchange client, String sy1, String sy2, String sBase) {
        new Thread(()->{
            SyncDo doBuy = new SyncDo(client,sy1,sy2,sBase,lock,"BUY");
            doBuy.setSType(2);
            doBuy.doIt();
        },sBase+"_"+sy1+"_"+sy2+"_BUY/MOVING").start();
        new Thread(()->{
            SyncDo doSell = new SyncDo(client,sy1,sy2,sBase,lock,"SELL");
            doSell.setSType(2);
            doSell.doIt();
        },sBase+"_"+sy1+"_"+sy2+"_SELL/MOVING").start();

    }
}
