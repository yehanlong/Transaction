package com.transaction.core.exchange.zhaobi;

// 例子：
// 用比特元换ycc  sy1 bty   sy2 ycc
// 1.买比特元 2.比特元换ycc 3.卖ycc
// 注意：第二步的type是买

import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

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
                double btyNum = sy1Market.getSells().get(0).getAm();
                BigDecimal btyPriceB = new BigDecimal(btyPrice);
                BigDecimal btyNumB = new BigDecimal(btyNum);
                int btyB = (btyPriceB.multiply(btyNumB)).compareTo(new BigDecimal(2.0));
                if(btyB == -1 || !sy1Market.getSuccess()){
                    // 当获取失败或者金额太少，就放弃此次循环
                    Thread.sleep(5000);
                    continue;
                }
                BigDecimal btyCountB = ((new BigDecimal(usdt)).divide(btyPriceB));
                //double btyCount = usdt/btyPrice;

                // bty 买ycc
                TradeVO sy12Market = client.getMarketInfo(sy2+sy1);
                double ybPrice = sy12Market.getSells().get(0).getPrice();
                double ybNum = sy12Market.getSells().get(0).getAm();
                BigDecimal ybPriceB =new BigDecimal(ybPrice);
                BigDecimal ybNumB = new BigDecimal(ybNum);
                int a = (ybPriceB.multiply(ybNumB)).multiply(btyPriceB).compareTo(new BigDecimal(2.0));
                if (a == -1 || !sy12Market.getSuccess()) {
                    Thread.sleep(5000);
                    continue;
                }
                BigDecimal yccCountB = btyCountB.divide(ybPriceB);
                //double yccCount = btyCount/ybPrice;

                // 卖掉ycc
                TradeVO sy2Market = client.getMarketInfo(sy2+"USDT");
                double yccPrice = sy2Market.getBuys().get(0).getPrice();
                double yccNum = sy2Market.getBuys().get(0).getAm();
                BigDecimal yccPriceB =new BigDecimal(yccPrice);
                BigDecimal yccNumB = new BigDecimal(yccNum);
                BigDecimal usdtCount = yccCountB.divide(yccPriceB);
                int a1 = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(2.0));
                if(a1 == -1 || !sy2Market.getSuccess()){
                    Thread.sleep(5000);
                    continue;
                }
                logger.debug("预计一轮usdt：",usdtCount);

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a2 = usdtCount.compareTo((new BigDecimal(usdt)).multiply((new BigDecimal(1)).add(new BigDecimal(0.0035))));
                if (a2 == 1) {

                    // 有盈利，开始交易

                    double everyUSDT = 2.0;
                    double accUSDT = client.getAccount("USDT").getActive();
                    int a3 = (new BigDecimal(accUSDT)).compareTo(new BigDecimal(2.0));
                    if (a3 == -1) {
                        logger.info("账户 usdt 小于 2.0");
                        Thread.sleep(5000);
                        continue;
                    }

                    logger.info("初始usdt：", accUSDT);

                    // 1.买入bty
                    TradeVO sy1Market1 = client.getMarketInfo(sy1+"USDT");
                    double btyPrice1 = sy1Market1.getSells().get(0).getPrice();
                    double btyNum1 = sy1Market1.getSells().get(0).getAm();
                    BigDecimal btyPrice1B = new BigDecimal(btyPrice1);
                    BigDecimal btyNum1B = new BigDecimal(btyNum1);
                    int a4 = ((btyPrice1B.multiply(btyNum1B)).compareTo(new BigDecimal(2.0)));
                    if(a4 == -1 || !sy1Market1.getSuccess()){
                        // 当获取失败或者金额太少，就放弃此次循环
                        Thread.sleep(5000);
                        logger.warn("btyPrice1*btyNum1 < 2.0 !sy1Market1.getSuccess 该订单部分被别人吃掉");
                        continue;
                    }
                    int a5 = btyPriceB.compareTo(btyPrice1B);
                    if (a5 != 0){
                        // 价格发生变化，立即进行下次循环
                        logger.warn("btyPrice != btyPrice1 该订单已经被别人吃掉");
                        continue;
                    }
                    BigDecimal btyCount1B  = (new BigDecimal(everyUSDT)).
                            multiply(((new BigDecimal(1)).subtract(new BigDecimal(0.001)))).divide(btyPrice1B);
                    //double btyCount1 = everyUSDT*(1-0.001)/btyPrice1;
                    int a6 = btyNum1B.compareTo(btyCount1B);
                    if(a6 == -1){
                        btyCount1B = btyNum1B;
                    }
                    // todo
//                    if (btyPrice1*btyNum1 > 2.0 && btyPrice1*btyNum1 < 4.0){
//                        btyCount1 = btyNum1;
//                    }
                    boolean success1 = client.postBill(btyCount1B.doubleValue(),sy1,"USDT",btyPrice1,"buy");
                    if (!success1){
                        logger.error("第一次交易挂单失败");
                        continue;
                    }

                    // 2.bty换ycc
                    TradeVO sy12Market1 = client.getMarketInfo(sy2+sy1);
                    double ybPrice1 = sy12Market1.getSells().get(0).getPrice();
                    double ybNum1 = sy12Market1.getSells().get(0).getAm();
                    BigDecimal ybPrice1B = new BigDecimal(ybPrice1);
                    BigDecimal ybNum1B = new BigDecimal(ybNum1);
                    if (!sy12Market1.getSuccess()) {
                        Thread.sleep(5000);
                        continue;
                    }
                    // 发现价格变化还是继续执行，亏也就亏一次
                    int a7 = (ybPrice1B.multiply(ybNum1B)).compareTo(new BigDecimal(2.0));
                    if (a7 == -1) {
                        logger.warn("ybPrice1*ybNum1 < 2.0 该订单部分被别人吃掉");
                    }
                    int a8 = ybPriceB.compareTo(ybPrice1B);
                    if (a8 != 0) {
                        logger.warn("ybPrice != ybPrice1 该订单已经被别人吃掉");
                        //continue;
                    }

                    double accBTY = client.getAccount(sy1).getActive();
                    BigDecimal accBTYB = new BigDecimal(accBTY);
                    int a9 = accBTYB.compareTo(btyCount1B);
                    if (a9 == -1) {
                        logger.warn("accBTY < btyCount1");
                        btyCount1B = accBTYB;
                    }
                    BigDecimal yccCount1B = btyCount1B.multiply((new BigDecimal(1).subtract(new BigDecimal(0.001))))
                            .divide(ybPrice1B);
                    //double yccCount1 = btyCount1*(1-0.001)/ybPrice1;
                    int a10 = ybNum1B.compareTo(yccCount1B);
                    if(a10 == -1){
                        logger.warn("ybNum1 < yccCount1 该订单部分被别人吃掉");
                        yccCount1B = ybNum1B;
                    }
                    Boolean success2 = client.postBill(yccCount1B.doubleValue(),sy2,sy1,ybPrice1,"buy");
                    if (!success2){
                        logger.error("第二次交易挂单失败");
                        continue;
                    }

                    // 卖掉ycc
                    TradeVO sy2Market1 = client.getMarketInfo(sy2+"USDT");
                    double yccPrice1 = sy2Market1.getBuys().get(0).getPrice();
                    double yccNum1 = sy2Market1.getBuys().get(0).getAm();
//                    double usdtcount1 = yccCount*yccPrice;
                    BigDecimal yccPrice1B = new BigDecimal(yccPrice1);
                    BigDecimal yccNum1B = new BigDecimal(yccNum1);
                    if(!sy2Market1.getSuccess()){
                        Thread.sleep(5000);
                        continue;
                    }
                    int a11 = (yccPrice1B.multiply(yccNum1B)).compareTo(new BigDecimal(2.0));
                    if(a11 == -1) {
                        logger.warn("yccPrice1*yccNum1 < 2.0 该订单部分被别人吃掉");
                    }
                    int a12 = yccPrice1B.compareTo(yccPriceB);
                    if(a12 != 0) {
                        logger.warn("yccPrice1 != yccPrice 该订单已经被别人吃掉");
//                        continue;
                    }

                    double accYCC = client.getAccount(sy2).getActive();
                    BigDecimal accYCCB = new BigDecimal(accYCC);
                    int a13 = accYCCB.compareTo(yccCount1B);
                    if (a13 == -1)  {
                        logger.warn("accYCC < yccCount1");
                        yccCount1B = accYCCB;
                    }
                    Boolean success3 = client.postBill(yccCount1B.doubleValue(),sy2,"USDT",yccPrice1,"sell");
                    if (!success3){
                        logger.error("第三次交易挂单失败");
                        break;
                    }

                    double accUSDTEnd = client.getAccount("USDT").getActive();
                    logger.info("最终usdt： ", accUSDTEnd);
                    logger.info("此次盈利USDT: ", accUSDTEnd - accUSDT);
                }


            } catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();

            }
        }


    }
}
