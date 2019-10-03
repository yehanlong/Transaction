package com.transaction.core.exchange.pub;

// 获取行情信息，异步获取3个交易对信息。和挂单信息一致，不再每个交易所单独实现

import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MarketInfo {
    public static SyncMarkInfo syncGetMarketInfo(Exchange client, String symbol1, String symbol2, String SBase){
        CountDownLatch latch = new CountDownLatch(3);
        Lock lock = new ReentrantLock();
        SyncMarkInfo syncMarkInfo = new SyncMarkInfo();
        syncMarkInfo.setLock(lock);


        new Thread(()->{
            getMarketInfo(client,symbol1,SBase,1,syncMarkInfo,latch);
        }).start();

        new Thread(()->{
            getMarketInfo(client,symbol2,symbol1,2,syncMarkInfo,latch);
        }).start();

        new Thread(()->{
            getMarketInfo(client,symbol2,SBase,3,syncMarkInfo,latch);
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return syncMarkInfo;
    }


    public static void getMarketInfo(Exchange client, String sy1, String sy2, int count, SyncMarkInfo syncMarkInfo, CountDownLatch latch) {
        TradeVO tradeVO = client.getMarketInfo(sy1,sy2);
        syncMarkInfo.getLock().lock();
        if (count == 1) {
            syncMarkInfo.setTrade1(tradeVO);
        }else if (count == 2){
            syncMarkInfo.setTrade2(tradeVO);
        }else {
            syncMarkInfo.setTrade3(tradeVO);
        }
        syncMarkInfo.getLock().unlock();
        latch.countDown();
    }

}




