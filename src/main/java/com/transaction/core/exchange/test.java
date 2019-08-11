package com.transaction.core.exchange;

import com.transaction.core.exchange.zhaobi.Moving1;
import com.transaction.core.exchange.zhaobi.SyncMoving1;
import com.transaction.core.exchange.zhaobi.SyncMoving2;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class test {

    public static void main(String[] args) {
        ZhaobiClient ZBClient = new ZhaobiClient();
        Lock lock = new ReentrantLock();
//        SyncMoving2 m2 = new SyncMoving2(ZBClient, "BTY", "YCC");
//        m2.setLock(lock);
//        m2.run();

        SyncMoving1 m1 = new SyncMoving1(ZBClient, "BTY", "YCC");
        m1.setLock(lock);
        m1.run();
    }
}
