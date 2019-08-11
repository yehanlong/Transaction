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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

// 例子：
// 用比特元换ycc  sy1 bty   sy2 ycc
// 1.买比特元 2.比特元换ycc 3.卖ycc
// 注意：第二步的type是买

public class SyncMoving1 extends Thread {


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
        logger.info("moving1 start, sy1: " + sy1 +", sy2: "+ sy2);

        List<Double> HistoryUSDTList = new LinkedList<>();

        // 初始usdt
        double startUSDT = client.getAccount("USDT").getActive();
        HistoryUSDTList.add(startUSDT);


        while (true) {
            try {
//                lock.lock();

                double usdt = 4.0;

                double accUSDT = client.getAccount("USDT").getActive();
                if (accUSDT < 4.0) {
                    logger.info("账户usdt 小于 4.0");
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
                    logger.info("获取市场行情失败");
                    Thread.sleep(5000);
                    continue;
                }


                // 获取bty价格
                TradeVO BTYMarket = syncMarkInfo.getTrade1();
                // 获取bty-ycc价格
                TradeVO BTYYCCMarket = syncMarkInfo.getTrade2();
                // 获取ycc价格
                TradeVO YCCMarket = syncMarkInfo.getTrade3();

                double btyPrice = BTYMarket.getSells().get(0).getPrice();
                double btyNum = BTYMarket.getSells().get(0).getAm();

                double ybPrice = BTYYCCMarket.getSells().get(0).getPrice();
                double ybNum = BTYYCCMarket.getSells().get(0).getAm();

                double yccPrice = YCCMarket.getBuys().get(0).getPrice();
                double yccNum = YCCMarket.getBuys().get(0).getAm();


                AmountPrice amountPrice = new AmountPrice();
                amountPrice.setSy1Amount(btyNum);
                amountPrice.setSy1Price(btyPrice);

                amountPrice.setSy12Amount(ybNum);
                amountPrice.setSy12Price(ybPrice);

                amountPrice.setSy2Amount(yccNum);
                amountPrice.setSy2Price(yccPrice);

                BigDecimal usdtcountB = Deal.getUSDTcount(amountPrice,"BUY");
                BigDecimal usdtB = new BigDecimal(5.0);

                logger.info("预计一轮后的usdt：" + usdtcountB.doubleValue());

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(0.008)))));
                if (a3 == 1) {

                    // 有盈利，开始交易
                    double everyUSDT = 4.0;

                    // 获取此轮交易实际需要的USDT
                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice,  "BUY");

                    logger.info("此次挂单可吃的usdt数量: " + ap.getMinUSDT());

                    if (everyUSDT > ap.getMinUSDT()) {
                        // 直接吃完整个订单
                        // 一起执行3比交易
                        logger.info("直接吃完整个订单");
                        boolean b = client.syncPostBill(sy1, sy2, ap.getSy1Amount(), ap.getSy12Amount(), ap.getSy2Amount(), ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                        }
                    } else {
                        // 如果4.0太少了，只能一步步吃
                        // 一起执行3比交易
                        logger.info("一步步吃订单");
                        double point = everyUSDT / ap.getMinUSDT();
                        boolean b = client.syncPostBill(sy1, sy2, ap.getSy1Amount() * point, ap.getSy12Amount() * point, ap.getSy2Amount() * point,
                                ap.getSy1Price(), ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                        }
                    }

                    // 盈利统计，同时解决延迟问题
                    double lastUSDT = HistoryUSDTList.get(HistoryUSDTList.size() - 1);
                    double accUSDTEnd = client.getAccount("USDT").getActive();

                    // 相等说明挂单的价格延迟，既挂单的时候没扣钱  <-1  指卖出的钱没到账
                    for (int i= 0; i < 5; i++){
                        if (accUSDTEnd == accUSDT  || accUSDTEnd - accUSDT < -0.3) {
                            // 此处需要保证不受延迟影响
                            Thread.sleep(500);
                            accUSDTEnd = client.getAccount("USDT").getActive();
                            logger.info("获取余额延迟, 次数： " +  (i+1));
                        }
                    }

                    logger.info("初始usdt： " + lastUSDT);
                    logger.info("最终usdt： " + accUSDTEnd);
                    logger.info("此次盈利USDT: " + (accUSDTEnd - lastUSDT));
                    logger.info("USDT总盈利：" + (accUSDTEnd - HistoryUSDTList.get(0)));
                    HistoryUSDTList.add(accUSDTEnd);
                    // 可以打印下历史数据HistoryUSDTList
                }else {
                    Thread.sleep(3000);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                lock.unlock();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
