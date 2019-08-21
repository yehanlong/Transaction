package com.transaction.core.strategy;

import com.transaction.core.exchange.pubinterface.Exchange;
import org.springframework.data.jpa.repository.Lock;

public interface Strategy {

    // test的时候不用传锁
    void start(Lock lock, Exchange client, String sy1, String sy2, String SBase);
}
