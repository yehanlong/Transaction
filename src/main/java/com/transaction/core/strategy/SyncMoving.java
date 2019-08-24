package com.transaction.core.strategy;

import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;


// 一个交易所用一个锁

public class SyncMoving implements Strategy {
    @Override
    public void start(Lock lock, Exchange client, String sy1, String sy2, String sBase) {
        new Thread(()->{
            SyncDo syncDoBuy = new SyncDo(client,sy1,sy2,sBase,lock,"BUY");
            syncDoBuy.setSType(3);
            syncDoBuy.doIt();
        },sBase+"_"+sy1+"_"+sy2+"_BUY/SYNCMOVING").start();
        new Thread(()->{
            SyncDo syncDoSell = new SyncDo(client,sy1,sy2,sBase,lock,"SELL");
            syncDoSell.setSType(3);
            syncDoSell.doIt();
        },sBase+"_"+sy1+"_"+sy2+"_SELL/SYNCMOVING").start();

    }
}
