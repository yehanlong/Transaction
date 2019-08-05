package com.transaction.core.exchange.zhaobi;

// 例子：
// 用ycc换比特元
// 1.买ycc 2.ycc换比特元 3.卖比特元
// 注意：第二步的type是卖

import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;

public class Moving2 extends Thread {

    private Exchange client;
    private String sy1;
    private String sy2;
    private static Lock lock;
    public Exchange getClient() {
        return client;
    }

    public void setClient(Exchange client) {
        this.client = client;
    }

    public static Lock getLock() {
        return lock;
    }

    public static void setLock(Lock lock) {
        Moving2.lock = lock;
    }

    public String getSy1() {
        return sy1;
    }

    public void setSy1(String sy1) {
        this.sy1 = sy1;
    }

    public String getSy2() {
        return sy2;
    }

    public void setSy2(String sy2) {
        this.sy2 = sy2;
    }

    public Moving2(Exchange client, String sy1, String sy2) {
        this.client = client;
        this.sy1 = sy1;
        this.sy2 = sy2;
    }

    @Override
    public void run() {

        System.out.printf("moving2 start, sy1: %s, sy2: %s \n", sy1, sy2);

        while (true) {
            try {
                lock.lock();
                //System.out.println(222);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }




    }
}
