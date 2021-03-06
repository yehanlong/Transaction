//package com.transaction.core.strategy;
//
//// 例子：
//// 用ycc换比特元
//// 1.买ycc 2.ycc换比特元 3.卖比特元
//// 注意：第二步的type是卖
//
//import com.transaction.core.entity.vo.TradeVO;
//import com.transaction.core.exchange.pub.RestTemplateStatic;
//import com.transaction.core.exchange.pubinterface.Exchange;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.concurrent.locks.Lock;
//
//public class Moving2 extends Thread {
//
//    private Exchange client;
//    private String sy1;
//    private String sy2;
//    private static Lock lock;
//    public Exchange getClient() {
//        return client;
//    }
//
//    public void setClient(Exchange client) {
//        this.client = client;
//    }
//
//    public static Lock getLock() {
//        return lock;
//    }
//
//    public static void setLock(Lock lock) {
//        Moving2.lock = lock;
//    }
//
//    public String getSy1() {
//        return sy1;
//    }
//
//    public void setSy1(String sy1) {
//        this.sy1 = sy1;
//    }
//
//    public String getSy2() {
//        return sy2;
//    }
//
//    public void setSy2(String sy2) {
//        this.sy2 = sy2;
//    }
//
//    public Moving2(Exchange client, String sy1, String sy2) {
//        this.client = client;
//        this.sy1 = sy1;
//        this.sy2 = sy2;
//    }
//    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Override
//    public void run() {
//
//        logger.info("moving2 start, sy1: %s, sy2: %s \n", sy1, sy2);
//
//        while (true) {
//            try {
//                lock.lock();
//                double usdt = 2.0;
//
//                //ex:  s1 BTY  s2 YCC
//
//                // usdt 买ycc
//                TradeVO sy2Market = client.getMarketInfo(sy2+"USDT");
//                double yccPrice = sy2Market.getSells().get(0).getPrice();
//                double yccNum = sy2Market.getSells().get(0).getAm();
//                BigDecimal yccPriceB = new BigDecimal(yccPrice);
//                BigDecimal yccNumB = new BigDecimal(yccNum);
//                int a = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(2.0));
//                if(a == -1 || !sy2Market.getSuccess()){
//                    // 当获取失败或者金额太少，就放弃此次循环
//                    Thread.sleep(5000);
//                    continue;
//                }
//                //double yccCount = usdt/yccPrice;
//                BigDecimal usdtB = new BigDecimal(usdt);
//                BigDecimal yccCountB = usdtB.divide(yccPriceB,25, RoundingMode.HALF_DOWN);
//
//                // 获取bty价格
//                TradeVO sy1Market = client.getMarketInfo(sy1+"USDT");
//
//                // 卖掉ycc 换bty
//                TradeVO sy12Market = client.getMarketInfo(sy2+sy1);
//                double ybPrice = sy12Market.getBuys().get(0).getPrice();
//                double ybNum = sy12Market.getBuys().get(0).getAm();
//                BigDecimal ybPriceB = new BigDecimal(ybPrice);
//                BigDecimal ybNumB = new BigDecimal(ybNum);
//                int a1 = (ybPriceB.multiply(ybNumB).multiply(new BigDecimal(sy1Market.getBuys().get(0).getPrice())))
//                        .compareTo(new BigDecimal(2.0));
//                // bty数量*bty价格
//                if (a1 == -1 || !sy12Market.getSuccess()) {
//                    Thread.sleep(5000);
//                    continue;
//                }
//                // 最终获得的bty
//                //double btyCount = yccCount*ybPrice;
//                BigDecimal btyCountB = yccCountB.multiply(ybPriceB);
//
//                // 卖掉BTY
//
//                double btyPrice = sy1Market.getBuys().get(0).getPrice();
//                double btyNum = sy1Market.getBuys().get(0).getAm();
//                BigDecimal btyPriceB = new BigDecimal(btyPrice);
//                BigDecimal btyNumB = new BigDecimal(btyNum);
//                // 最终获得的usdt
//                //double usdtcount = btyPrice*btyCount;
//                BigDecimal usdtcountB = btyPriceB.multiply(btyCountB);
//                int a2 = (btyPriceB.multiply(btyNumB)).compareTo(new BigDecimal(2.0));
//                if(btyPrice*btyNum < 2.0 || !sy1Market.getSuccess()){
//                    Thread.sleep(5000);
//                    continue;
//                }
//                logger.debug("预计一轮usdt：",usdtcountB.doubleValue());
//
//                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
//                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(0.0035)))));
//                if (a3 == 1) {
//
//                    // 有盈利，开始交易
//
//                    double everyUSDT = 2.0;
//                    double accUSDT = client.getAccount().get("USDT").getActive();
//                    if (accUSDT < 2.0) {
//                        logger.info("账户usdt 小于 2.0");
//                        Thread.sleep(5000);
//                        continue;
//                    }
//
//                    logger.info("初始usdt：", accUSDT);
//
//                    // 1.买入ycc
//                    TradeVO sy2Market1 = client.getMarketInfo(sy2+"USDT");
//                    double yccPrice1 = sy2Market1.getSells().get(0).getPrice();
//                    double yccNum1 = sy2Market1.getSells().get(0).getAm();
//                    BigDecimal yccPrice1B = new BigDecimal(yccPrice1);
//                    BigDecimal yccNum1B = new BigDecimal(yccNum1);
//                    int a4 = (yccPrice1B.multiply(yccNum1B)).compareTo(new BigDecimal(2.0));
//                    if(a4 == -1 || !sy2Market1.getSuccess()){
//                        // 当获取失败或者金额太少，就放弃此次循环
//                        Thread.sleep(5000);
//                        logger.warn("yccPrice1*yccNum1 < 2.0 !sy2Market1.getSuccess 该订单部分被别人吃掉");
//                        continue;
//                    }
//                    int a5 = yccPriceB.compareTo(yccPrice1B);
//                    if (a5 != 0){
//                        // 价格发生变化，立即进行下次循环
//                        logger.warn("yccPrice != yccPrice1 该订单已经被别人吃掉");
//                        continue;
//                    }
//                    //double yccCount1 = everyUSDT*(1-0.001)/yccPrice1;
//                    BigDecimal everyUSDTB = new BigDecimal(everyUSDT);
//                    BigDecimal yccCount1B = everyUSDTB.multiply((new BigDecimal(1).subtract(new BigDecimal(0.001))))
//                            .divide(yccPrice1B,25,RoundingMode.HALF_DOWN);
//                    int a6 = yccNum1B.compareTo(yccCount1B);
//                    if(a6 == -1){
//                        yccCount1B = yccNum1B;
//                    }
//                    // todo
////                    if (btyPrice1*btyNum1 > 2.0 && btyPrice1*btyNum1 < 4.0){
////                        btyCount1 = btyNum1;
////                    }
//                    boolean success1 = client.postBill(yccCount1B.doubleValue(),sy2,"USDT",yccPrice1,"buy");
//                    if (!success1){
//                        logger.error("第一次交易挂单失败");
//                        continue;
//                    }
//
//                    if(success1){
//                        logger.info("买入ycc，该价格的原始挂单币数量"+yccNum1);
//                        logger.info("买入ycc，该价格的成交币数量"+yccCount1B.doubleValue());
//                    }
//                    // 2.b卖掉ycc  换bty
//                    TradeVO sy12Market1 = client.getMarketInfo(sy2+sy1);
//                    double ybPrice1 = sy12Market1.getBuys().get(0).getPrice();
//                    double ybNum1 = sy12Market1.getBuys().get(0).getAm();
//                    BigDecimal ybPrice1B = new BigDecimal(ybPrice1);
//                    BigDecimal ybNum1B = new BigDecimal(ybNum1);
//                    if (!sy12Market1.getSuccess()) {
//                        Thread.sleep(5000);
//                        continue;
//                    }
//                    // 发现价格变化还是继续执行，亏也就亏一次
//                    int a7 = (ybPrice1B.multiply(ybNum1B).multiply(btyPriceB)).compareTo(new BigDecimal(2.0));
//                    if (a7 == -1) {
//                        logger.warn("ybPrice1*ybNum1 < 2.0 该订单部分被别人吃掉");
//                    }
//                    int a8 = ybPriceB.compareTo(ybPrice1B);
//                    if (a8 != 0) {
//                        logger.warn("ybPrice != ybPrice1 该订单已经被别人吃掉");
//                        //continue;
//                    }
//
//                    double accYCC = client.getAccount().get(sy2).getActive();
//                    BigDecimal accYCCB = new BigDecimal(accYCC);
//                    int a9 = accYCCB.compareTo(yccCount1B);
//                    if (a9 == -1) {
//                        logger.warn("accYCC < yccCount1 账户余额不足");
//                        yccCount1B = accYCCB;
//                    }
//                    //double btyCount1 = yccCount1*(1-0.001)*ybPrice1;
//                    BigDecimal btyCount1B = yccCount1B.multiply((new BigDecimal(1)).subtract(new BigDecimal(0.001))).multiply(ybPrice1B);
//                    int a10 = ybNum1B.compareTo(yccCount1B);
//                    if(a10 == -1){
//                        logger.warn("ybNum1 < yccCount1 该订单部分被别人吃掉");
//                        yccCount1B = ybNum1B;
//                    }
//                    Boolean success2 = client.postBill(yccCount1B.doubleValue(),sy2,sy1,ybPrice1,"sell");
//                    if (!success2){
//                        logger.error("第二次交易挂单失败");
//                        continue;
//                    }
//
//                    // 卖掉bty
//                    TradeVO sy1Market1 = client.getMarketInfo(sy1+"USDT");
//                    double btyPrice1 = sy1Market1.getBuys().get(0).getPrice();
//                    double btyNum1 = sy1Market1.getBuys().get(0).getAm();
////                    double usdtcount1 = yccCount*yccPrice;
//                    BigDecimal btyPrice1B = new BigDecimal(btyPrice1);
//                    BigDecimal btyNum1B = new BigDecimal(btyNum1);
//                    if(!sy1Market1.getSuccess()){
//                        Thread.sleep(5000);
//                        continue;
//                    }
//                    int a11 = (btyPrice1B.multiply(btyNum1B)).compareTo(new BigDecimal(2.0));
//                    if(a11 == -1) {
//                        logger.warn("btyPrice1*btyNum1 < 2.0 该订单部分被别人吃掉");
//                    }
//                    int a12 = btyPrice1B.compareTo(btyPriceB);
//                    if(a12 != 0) {
//                        logger.warn("yccPrice1 != yccPrice 该订单已经被别人吃掉");
////                        continue;
//                    }
//
//                    double accBTY = client.getAccount().get(sy1).getActive();
//                    BigDecimal accBTYB = new BigDecimal(accBTY);
//                    int a13 = accBTYB.compareTo(btyCount1B);
//                    if (a13 == -1)  {
//                        logger.warn("accBTY < btyCount1");
//                        btyCount1B = accBTYB;
//                    }
//                    Boolean success3 = client.postBill(btyCount1B.doubleValue(),sy1,"USDT",yccPrice1,"sell");
//                    if (!success3){
//                        logger.error("第三次交易挂单失败");
//                        break;
//                    }
//
//                    double accUSDTEnd = client.getAccount().get("USDT").getActive();
//                    logger.info("最终usdt： ", accUSDTEnd);
//                    logger.info("此次盈利USDT: ", accUSDTEnd - accUSDT);
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                lock.unlock();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//
//
//    }
//}
