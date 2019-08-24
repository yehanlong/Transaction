package com.transaction.core.strategy;

import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;

public class Moving implements Strategy{
    @Override
    public void start(Lock lock, Exchange client, String sy1, String sy2, String sBase) {

        SyncDo doBuy = new SyncDo(client,sy1,sy2,sBase,lock,"BUY");
        doBuy.setSType(2);
        doBuy.doIt();
        SyncDo doSell = new SyncDo(client,sy1,sy2,sBase,lock,"Sell");
        doSell.setSType(2);
        doSell.doIt();
    }
}
