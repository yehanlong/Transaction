package com.transaction.core.entity;

import com.transaction.core.entity.vo.TradeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.Lock;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SyncMarkInfo {
    private TradeVO trade1;
    private TradeVO trade2;
    private TradeVO trade3;
    private Lock lock;


    public TradeVO getTrade1() {
        return trade1;
    }

    public void setTrade1(TradeVO trade1) {
        this.trade1 = trade1;
    }

    public TradeVO getTrade2() {
        return trade2;
    }

    public void setTrade2(TradeVO trade2) {
        this.trade2 = trade2;
    }

    public TradeVO getTrade3() {
        return trade3;
    }

    public void setTrade3(TradeVO trade3) {
        this.trade3 = trade3;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }
}
