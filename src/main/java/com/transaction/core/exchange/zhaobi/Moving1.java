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

                    double everyUSDT = 2.0;
                    double accUSDT = client.getAccount("USDT").getActive();
                    if (accUSDT < 2.0) {
                        System.out.println("账户 usdt 小于 4.0");
                        continue;
                    }

                    // 1.买入bty
                    TradeVO sy1Market1 = client.getMarketInfo(sy1+"USDT");
                    double btyPrice1 = sy1Market1.getSells().get(0).getPrice();
                    double btyNum1 = sy1Market1.getSells().get(0).getCount();
                    if(btyPrice1*btyNum1 < 2.0 || !sy1Market1.getSuccess()){
                        // 当获取失败或者金额太少，就放弃此次循环
                        Thread.sleep(5000);
                        continue;
                    }
                    if (btyPrice != btyPrice1){
                        // 价格发生变化，立即进行下次循环
                        continue;
                    }
                    double btyCount1 = everyUSDT*(1-0.001)/btyPrice1;
                    if(btyNum1<btyCount1){
                        btyCount1 = btyNum1;
                    }
                    // todo
//                    if (btyPrice1*btyNum1 > 2.0 && btyPrice1*btyNum1 < 4.0){
//                        btyCount1 = btyNum1;
//                    }
                    boolean success1 = client.postBill(btyCount1,sy1,"USDT",btyPrice1,"buy");
                    if (!success1){
                        break;
                    }

                    // 2.bty换ycc
                    TradeVO sy12Market1 = client.getMarketInfo(sy2+sy1);
                    double ybPrice1 = sy12Market1.getSells().get(0).getPrice();
                    double ybNum1 = sy12Market1.getSells().get(0).getCount();
                    if (ybPrice1*ybNum1 < 2.0 || !sy12Market1.getSuccess()) {
                        Thread.sleep(5000);
                        continue;
                    }
                    if (ybPrice != ybPrice1) {
                        continue;
                    }

                    double accBTY = client.getAccount("BTY").getActive();
                    if (accBTY < btyCount1) {
                        btyCount1 = accBTY;
                    }
                    double yccCount1 = btyCount1*(1-0.001)/ybPrice;
                    if(ybNum1 < yccCount){
                        yccCount1 = ybNum1;
                    }
                    Boolean success2 = client.postBill(yccCount1,sy2,sy1,ybPrice1,"buy");
                    if (!success2){
                        break;
                    }

                    // 卖掉ycc
                    TradeVO sy2Market1 = client.getMarketInfo(sy2+"USDT");
                    double yccPrice1 = sy2Market1.getBuys().get(0).getPrice();
                    double yccNum1 = sy2Market1.getBuys().get(0).getCount();
//                    double usdtcount1 = yccCount*yccPrice;
                    if(yccPrice1*yccNum1 < 2.0 || !sy2Market1.getSuccess()){
                        Thread.sleep(5000);
                        continue;
                    }
                    if(yccPrice1 != yccPrice) {
                        continue;
                    }

                    double accYCC = client.getAccount("YCC").getActive();
                    if (accYCC < yccCount1)  {
                        yccCount1 = accYCC;
                    }
                    Boolean success3 = client.postBill(yccCount1,sy2,"USDT",yccPrice1,"sell");
                    if (!success3){
                        break;
                    }
                }


            } catch (Exception e){

            }finally {
                lock.unlock();

            }
        }


    }
}
