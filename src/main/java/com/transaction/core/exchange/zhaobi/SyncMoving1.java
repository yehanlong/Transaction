package com.transaction.core.exchange.zhaobi;

import com.transaction.core.entity.AmountPrice;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class SyncMoving1  extends Thread {


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

    public SyncMoving1(Exchange client, String sy1, String sy2) {
        this.client = client;
        this.sy1 = sy1;
        this.sy2 = sy2;
    }
    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void run() {
        logger.info("moving2 start, sy1: %s, sy2: %s \n", sy1, sy2);

        List<Double> HistoryUSDTList = new LinkedList<>();

        // 初始usdt
        double startUSDT = client.getAccount("USDT").getActive();
        HistoryUSDTList.add(startUSDT);


        while (true) {
            try {
                lock.lock();
                double usdt = 2.0;
                double accUSDT = client.getAccount("USDT").getActive();
                if (accUSDT < 2.0) {
                    logger.info("账户usdt 小于 2.0");
                    Thread.sleep(5000);
                    continue;
                }

                //ex:  s1 BTY  s2 YCC

                // 异步获取市场行情 symbol1: BTY   symbol2:YCC
                // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
                SyncMarkInfo syncMarkInfo = client.getSyncMarkInfo(sy1, sy2);

                if (syncMarkInfo.getTrade1().getSuccess() && syncMarkInfo.getTrade2().getSuccess() && syncMarkInfo.getTrade3().getSuccess()) {
                    // 都获取成功
                } else {
                    Thread.sleep(5000);
                    continue;
                }


                // 获取bty价格
                TradeVO BTYMarket = syncMarkInfo.getTrade2();
                // 获取bty-ycc价格
                TradeVO BTYYCCMarket = syncMarkInfo.getTrade2();
                // 获取ycc价格
                TradeVO YCCMarket = syncMarkInfo.getTrade3();


                // 第一步
                // usdt 买ycc
                double yccPrice = YCCMarket.getSells().get(0).getPrice();
                double yccNum = YCCMarket.getSells().get(0).getAm();
                BigDecimal yccPriceB = new BigDecimal(yccPrice);
                BigDecimal yccNumB = new BigDecimal(yccNum);
                int a = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(2.0));
                if (a == -1) {
                    // 当获取失败或者金额太少，就放弃此次循环
                    Thread.sleep(5000);
                    continue;
                }
                //double yccCount = usdt/yccPrice;
                BigDecimal usdtB = new BigDecimal(usdt);
                BigDecimal yccCountB = usdtB.divide(yccPriceB, 25, RoundingMode.HALF_DOWN);


                // 第二步
                // 卖掉ycc 换bty
                double ybPrice = BTYYCCMarket.getBuys().get(0).getPrice();
                double ybNum = BTYYCCMarket.getBuys().get(0).getAm();
                BigDecimal ybPriceB = new BigDecimal(ybPrice);
                BigDecimal ybNumB = new BigDecimal(ybNum);
                int a1 = (ybPriceB.multiply(ybNumB).multiply(new BigDecimal(BTYMarket.getBuys().get(0).getPrice())))
                        .compareTo(new BigDecimal(2.0));
                // bty数量*bty价格
                if (a1 == -1) {
                    Thread.sleep(5000);
                    continue;
                }
                // 最终获得的bty
                //double btyCount = yccCount*ybPrice;
                BigDecimal btyCountB = yccCountB.multiply(ybPriceB);


                // 第三步
                // 卖掉BTY
                double btyPrice = BTYMarket.getBuys().get(0).getPrice();
                double btyNum = BTYMarket.getBuys().get(0).getAm();
                BigDecimal btyPriceB = new BigDecimal(btyPrice);
                BigDecimal btyNumB = new BigDecimal(btyNum);
                // 最终获得的usdt
                //double usdtcount = btyPrice*btyCount;
                BigDecimal usdtcountB = btyPriceB.multiply(btyCountB);
                int a2 = (btyPriceB.multiply(btyNumB)).compareTo(new BigDecimal(2.0));
                if (a2 == -1) {
                    Thread.sleep(5000);
                    continue;
                }


                logger.debug("预计一轮后的usdt：", usdtcountB.doubleValue());

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(0.0035)))));
                if (a3 == 1) {

                    // 有盈利，开始交易
                    double everyUSDT = 2.0;


                    AmountPrice amountPrice = new AmountPrice();
                    amountPrice.setSy1Amount(btyNum);
                    amountPrice.setSy1Price(btyPrice);

                    amountPrice.setSy12Amount(ybNum);
                    amountPrice.setSy12Price(ybPrice);

                    amountPrice.setSy2Amount(ybNum);
                    amountPrice.setSy2Price(ybPrice);

                    // 获取此轮交易实际需要的USDT
                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice, btyNum * btyPrice,
                            ybNum * ybPrice * btyPrice, yccNum * yccPrice, "SELL");
                    if (everyUSDT > ap.getMinUSDT()) {
                        // 直接吃完整个订单
                        // 一起执行3比交易
                        client.syncPostBill(sy1, sy2, ap.getSy1Amount(), ap.getSy12Amount(), ap.getSy2Amount(), ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), "SELL");
                    } else {
                        // 如果2.0太少了，只能一步步吃
                        // 一起执行3比交易
                        double point = everyUSDT / ap.getMinUSDT();
                        client.syncPostBill(sy1, sy2, ap.getSy1Amount() * point, ap.getSy12Amount() * point, ap.getSy2Amount() * point,
                                ap.getSy1Price(), ap.getSy12Price(), ap.getSy2Price(), "SELL");
                    }


                    double lastUSDT = HistoryUSDTList.get(HistoryUSDTList.size() - 1);
                    double accUSDTEnd = client.getAccount("USDT").getActive();
                    if (accUSDTEnd - accUSDT < -1) {
                        // 此处需要保证不受延迟影响
                        accUSDTEnd = client.getAccount("USDT").getActive();
                    }
                    logger.info("最终usdt： ", accUSDTEnd);
                    logger.info("此次盈利USDT: ", accUSDTEnd - accUSDT);
                    HistoryUSDTList.add(accUSDTEnd);
                    // 可以打印下历史数据HistoryUSDTList
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
