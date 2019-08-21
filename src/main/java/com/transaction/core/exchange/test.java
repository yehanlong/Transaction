package com.transaction.core.exchange;

import com.transaction.core.exchange.zt.MovingBuy;
import com.transaction.core.exchange.zt.ZTClient;

public class test {

    public static void main(String[] args) {
//        ZhaobiClient ZBClient = new ZhaobiClient();
//        Lock lock = new ReentrantLock();
////        SyncMoving2 m2 = new SyncMoving2(ZBClient, "BTY", "YCC");
////        m2.setLock(lock);
////        m2.run();
//
//        SyncMoving1 m1 = new SyncMoving1(ZBClient, "ETH", "YCC");
//        m1.setLock(lock);
//        m1.run();


        ZTClient ztClient = new ZTClient();


        MovingBuy m1 = new MovingBuy(ztClient, "BTC" , "EOS");
        m1.run();
    }
}
