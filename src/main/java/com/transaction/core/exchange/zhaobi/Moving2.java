package com.transaction.core.exchange.zhaobi;

// 例子：
// 用ycc换比特元
// 1.买ycc 2.ycc换比特元 3.卖比特元
// 注意：第二步的type是卖

import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void run() {

        System.out.printf("moving2 start, sy1: %s, sy2: %s \n", sy1, sy2);

        while (true) {
            try {
                lock.lock();
                double usdt = 2.0;

                // usdt 买比特元
                TradeVO sy1Market = client.getMarketInfo(sy1+"USDT");
                double btyPrice = sy1Market.getSells().get(0).getPrice();
                double btyNum = sy1Market.getSells().get(0).getAm();
                if(btyPrice*btyNum < 2.0 || !sy1Market.getSuccess()){
                    // 当获取失败或者金额太少，就放弃此次循环
                    Thread.sleep(5000);
                    continue;
                }
                double btyCount = usdt/btyPrice;

                // bty 买ycc
                TradeVO sy12Market = client.getMarketInfo(sy2+sy1);
                double ybPrice = sy12Market.getSells().get(0).getPrice();
                double ybNum = sy12Market.getSells().get(0).getAm();
                if (ybPrice*ybNum < 2.0 || !sy12Market.getSuccess()) {
                    Thread.sleep(5000);
                    continue;
                }
                double yccCount = btyCount/ybPrice;

                // 卖掉ycc
                TradeVO sy2Market = client.getMarketInfo(sy2+"USDT");
                double yccPrice = sy2Market.getBuys().get(0).getPrice();
                double yccNum = sy2Market.getBuys().get(0).getAm();
                double usdtcount = yccCount*yccPrice;
                if(yccPrice*yccNum < 2.0 || !sy2Market.getSuccess()){
                    Thread.sleep(5000);
                    continue;
                }
                logger.debug("预计一轮usdt：",usdtcount);

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                if (usdtcount > usdt*(1+0.0035)) {

                    // 有盈利，开始交易

                    double everyUSDT = 2.0;
                    double accUSDT = client.getAccount("USDT").getActive();
                    if (accUSDT < 2.0) {
                        logger.info("账户usdt 小于 2.0");
                        Thread.sleep(5000);
                        continue;
                    }

                    logger.info("初始usdt：", accUSDT);

                    // 1.买入bty
                    TradeVO sy1Market1 = client.getMarketInfo(sy1+"USDT");
                    double btyPrice1 = sy1Market1.getSells().get(0).getPrice();
                    double btyNum1 = sy1Market1.getSells().get(0).getAm();
                    if(btyPrice1*btyNum1 < 2.0 || !sy1Market1.getSuccess()){
                        // 当获取失败或者金额太少，就放弃此次循环
                        Thread.sleep(5000);
                        logger.warn("btyPrice1*btyNum1 < 2.0 !sy1Market1.getSuccess 该订单部分被别人吃掉");
                        continue;
                    }
                    if (btyPrice != btyPrice1){
                        // 价格发生变化，立即进行下次循环
                        logger.warn("btyPrice != btyPrice1 该订单已经被别人吃掉");
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
                        logger.error("第一次交易挂单失败");
                        continue;
                    }

                    // 2.bty换ycc
                    TradeVO sy12Market1 = client.getMarketInfo(sy2+sy1);
                    double ybPrice1 = sy12Market1.getSells().get(0).getPrice();
                    double ybNum1 = sy12Market1.getSells().get(0).getAm();
                    if (!sy12Market1.getSuccess()) {
                        Thread.sleep(5000);
                        continue;
                    }
                    // 发现价格变化还是继续执行，亏也就亏一次
                    if (ybPrice1*ybNum1 < 2.0) {
                        logger.warn("ybPrice1*ybNum1 < 2.0 该订单部分被别人吃掉");
                    }
                    if (ybPrice != ybPrice1) {
                        logger.warn("ybPrice != ybPrice1 该订单已经被别人吃掉");
                        //continue;
                    }

                    double accBTY = client.getAccount("BTY").getActive();
                    if (accBTY < btyCount1) {
                        logger.warn("accBTY < btyCount1");
                        btyCount1 = accBTY;
                    }
                    double yccCount1 = btyCount1*(1-0.001)/ybPrice1;
                    if(ybNum1 < yccCount1){
                        logger.warn("ybNum1 < yccCount1 该订单部分被别人吃掉");
                        yccCount1 = ybNum1;
                    }
                    Boolean success2 = client.postBill(yccCount1,sy2,sy1,ybPrice1,"buy");
                    if (!success2){
                        logger.error("第二次交易挂单失败");
                        continue;
                    }

                    // 卖掉ycc
                    TradeVO sy2Market1 = client.getMarketInfo(sy2+"USDT");
                    double yccPrice1 = sy2Market1.getBuys().get(0).getPrice();
                    double yccNum1 = sy2Market1.getBuys().get(0).getAm();
//                    double usdtcount1 = yccCount*yccPrice;
                    if(!sy2Market1.getSuccess()){
                        Thread.sleep(5000);
                        continue;
                    }
                    if(yccPrice1*yccNum1 < 2.0 ) {
                        logger.warn("yccPrice1*yccNum1 < 2.0 该订单部分被别人吃掉");
                    }
                    if(yccPrice1 != yccPrice) {
                        logger.warn("yccPrice1 != yccPrice 该订单已经被别人吃掉");
                        continue;
                    }

                    double accYCC = client.getAccount("YCC").getActive();
                    if (accYCC < yccCount1)  {
                        logger.warn("accYCC < yccCount1");
                        yccCount1 = accYCC;
                    }
                    Boolean success3 = client.postBill(yccCount1,sy2,"USDT",yccPrice1,"sell");
                    if (!success3){
                        logger.error("第三次交易挂单失败");
                        break;
                    }

                    double accUSDTEnd = client.getAccount("USDT").getActive();
                    logger.info("最终usdt： ", accUSDTEnd);
                    logger.info("此次盈利USDT: ", accUSDTEnd - accUSDT);
                }


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
