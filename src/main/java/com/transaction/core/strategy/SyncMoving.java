package com.transaction.core.strategy;

import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;


// 一个交易所用一个锁

public class SyncMoving implements Strategy {
    @Override
    public void start(Lock lock, Exchange client, String sy1, String sy2, String sBase) {

        SyncDo syncDo = new SyncDo(client,sy1,sy2,sBase,lock,"BUY");
        syncDo.doIt();
    }
}
