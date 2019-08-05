package com.transaction.core.exchange.zhaobi;

// 例子：
// 用比特元换ycc  sy1 bty   sy2 ycc
// 1.买比特元 2.比特元换ycc 3.卖ycc
// 注意：第二步的type是买

import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Moving1 extends Thread {

    private Exchange client;
    private String sy1;
    private String sy2;
    private Lock lock;

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Exchange getClient() {
        return client;
    }

    public void setClient(Exchange client) {
        this.client = client;
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

    public Moving1(Exchange client, String sy1, String sy2) {
        this.client = client;
        this.sy1 = sy1;
        this.sy2 = sy2;
    }

    @Override
    public void run() {

        //System.out.printf("moving1 start, sy1: %s, sy2: %s \n", sy1, sy2);

        while (true) {
            try {
                lock.lock();

                double usdt = 2.0;

                // usdt 买比特元
                TradeVO sy1Market = client.getMarketInfo(sy1+"USDT");
                double btyPrice = sy1Market.getSells().get(0).getPrice();
                double btyNum = sy1Market.getSells().get(0).getCount();
                if(btyPrice*btyNum < 2.0 || !sy1Market.getSuccess()){
                    // 当获取失败或者金额太少，就放弃此次循环
                    Thread.sleep(5000);
                    continue;
                }
                double btyCount = usdt/btyPrice;

                // bty 买ycc
                TradeVO sy12Market = client.getMarketInfo(sy2+sy1);
                double ybPrice = sy12Market.getSells().get(0).getPrice();
                double ybNum = sy12Market.getSells().get(0).getCount();
                if (ybPrice*ybNum < 2.0 || !sy12Market.getSuccess()) {
                    Thread.sleep(5000);
                    continue;
                }
                double yccCount = btyCount/ybPrice;

                // 卖掉ycc
                TradeVO sy2Market = client.getMarketInfo(sy2+"USDT");
                double yccPrice = sy2Market.getBuys().get(0).getPrice();
                double yccNum = sy2Market.getBuys().get(0).getCount();
                double usdtcount = yccCount*yccPrice;
                if(yccPrice*yccNum < 2.0 || !sy2Market.getSuccess()){
                    Thread.sleep(5000);
                    continue;
                }
                System.out.println(usdtcount);

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                if (usdtcount > usdt*(1+0.0035)) {

                    // 有盈利，开始交易
                    if (btyPrice*)
                }


            } catch (Exception e){

            }finally {
                lock.unlock();

            }
        }


    }
}
