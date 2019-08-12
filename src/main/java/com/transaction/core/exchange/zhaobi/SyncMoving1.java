package com.transaction.core.exchange.zhaobi;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.transaction.core.entity.AmountPrice;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.utils.DoubleUtil;
import com.transaction.core.utils.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Email;
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
        info("moving1 start, sy1: " + sy1 +", sy2: "+ sy2);

        List<Double> HistoryUSDTList = new LinkedList<>();
        double allMoney = 0.0;
        double thisMoney = 0.0;
        int count = 0;
        int in = 0;
        int emailStartMark = 0;
        double succUsdt = 0;

        // 初始usdt
        double startUSDT = client.getAccount("USDT").getActive();
        HistoryUSDTList.add(startUSDT);


        while (true) {

            try {
                lock.lock();
                in = 0;

                double usdt = 4.0;

                double accUSDT = client.getAccount("USDT").getActive();
                int a = DoubleUtil.compareTo(accUSDT,usdt);
                if (a == -1) {
                    info("账户usdt 小于 4.0");
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
                    info("获取市场行情失败");
                    Thread.sleep(5000);
                    continue;
                }


                // 获取bty价格
                TradeVO BTYMarket = syncMarkInfo.getTrade1();
                // 获取bty-ycc价格
                TradeVO BTYYCCMarket = syncMarkInfo.getTrade2();
                // 获取ycc价格
                TradeVO YCCMarket = syncMarkInfo.getTrade3();

                Order btyO = Deal.dealSmallOrder(BTYMarket.getSells());
                double btyPrice = btyO.getPrice();
                double btyNum = btyO.getAm();

                Order ybO = Deal.dealSmallOrder(BTYYCCMarket.getSells());
                double ybPrice = ybO.getPrice();
                double ybNum = ybO.getAm();

                Order yccO = Deal.dealSmallOrder(YCCMarket.getBuys());
                double yccPrice = yccO.getPrice();
                double yccNum = yccO.getAm();


                AmountPrice amountPrice = new AmountPrice();
                amountPrice.setSy1Amount(btyNum);
                amountPrice.setSy1Price(btyPrice);

                amountPrice.setSy12Amount(ybNum);
                amountPrice.setSy12Price(ybPrice);

                amountPrice.setSy2Amount(yccNum);
                amountPrice.setSy2Price(yccPrice);

                BigDecimal usdtcountB = Deal.getUSDTcount(amountPrice,"BUY");
                BigDecimal usdtB = new BigDecimal(5.0);

                info("预计一轮后的usdt：" + usdtcountB.doubleValue());

                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(0.008)))));
                if (a3 == 1) {
                    in = 1;
                    // 有盈利，开始交易

                    // 获取此轮交易实际需要的USDT
                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice,  "BUY");

                    info("此次挂单可吃的usdt数量: " + ap.getMinUSDT());

                    // 获取每次usdt
                    double everyUSDT = 4.0;
                    if(sy1 == "BTY" && sy2 == "YCC"){
                        everyUSDT = Deal.getEveryUsdt(usdtcountB.doubleValue(),ap.getMinUSDT(),0.0);
                    }else {
                        everyUSDT = Deal.getEveryUsdt(usdtcountB.doubleValue(),ap.getMinUSDT(),2.0);
                    }
                    // 此处是为了保证吧小单吃完
                    if (DoubleUtil.compareTo(ap.getMinUSDT() - everyUSDT, 1.5) == -1){
                        everyUSDT = ap.getMinUSDT();
                    }


                    int a1 = DoubleUtil.compareTo(everyUSDT,ap.getMinUSDT());

                    if (a1==1) {
                        // 直接吃完整个订单
                        // 一起执行3比交易
                        succUsdt += ap.getMinUSDT();
                        info("直接吃完整个订单");
                        boolean b = client.syncPostBill(sy1, sy2, ap.getSy1Amount(), ap.getSy12Amount(), ap.getSy2Amount(), ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                        }
                    } else {
                        // 如果4.0太少了，只能一步步吃
                        // 一起执行3比交易
                        succUsdt += everyUSDT;
                        info("一步步吃订单");
                        //double point = everyUSDT / ap.getMinUSDT();
                        double point = DoubleUtil.div(everyUSDT,ap.getMinUSDT(),25);
                        boolean b = client.syncPostBill(sy1, sy2, ap.getSy1Amount() * point, ap.getSy12Amount() * point, ap.getSy2Amount() * point,
                                ap.getSy1Price(), ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                        }
                    }

                    // 盈利统计，同时解决延迟问题
                    double lastUSDT = HistoryUSDTList.get(HistoryUSDTList.size() - 1);
                    double accUSDTEnd = client.getAccount("USDT").getActive();
                    int a4 = DoubleUtil.compareTo(accUSDTEnd,accUSDT);
                    int a5 = DoubleUtil.compareTo(DoubleUtil.sub(accUSDTEnd,accUSDT),-0.3);

                    // 利用延迟时间， 在此处发邮件  或者数据库操作
                    if (emailStartMark == 0) {
                        MailUtil.sendEmains("交易对"+sy1+sy2+" BUY触发, 第一次预计的usdt为"
                                +usdtcountB.doubleValue()+", 第一次预估此次可吃usdt为"+ap.getMinUSDT());
                        emailStartMark = 1;
                    }



                    // 相等说明挂单的价格延迟，既挂单的时候没扣钱  <-1  指卖出的钱没到账
                    for (int i= 0; i < 5; i++){
                        if (a4 == 1  || a5 == -1) {
                            // 此处需要保证不受延迟影响
                            Thread.sleep(500);
                            accUSDTEnd = client.getAccount("USDT").getActive();
                            info("获取余额延迟, 次数： " +  (i+1));
                        }
                    }

                    count++;
                    // 此次进入循环直到退出的盈利
                    thisMoney = thisMoney + DoubleUtil.sub(accUSDTEnd,lastUSDT);
                    // 此交易对在程序运行期间的总盈利
                    allMoney = allMoney + DoubleUtil.sub(accUSDTEnd,lastUSDT);


                    logger.info("初始usdt： " + lastUSDT);
                    logger.info("最终usdt： " + accUSDTEnd);
                    logger.info("此次盈利USDT: " + DoubleUtil.sub(accUSDTEnd,lastUSDT));
                    logger.info("USDT总盈利：" + DoubleUtil.sub(accUSDTEnd,HistoryUSDTList.get(0)));
                    HistoryUSDTList.add(accUSDTEnd);
                    // 可以打印下历史数据HistoryUSDTList

                }else {
                    Thread.sleep(3000);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in == 0){
                    lock.unlock();

                    // 发送结果报告
                    if (succUsdt != 0) {
                        // 发送最终结果邮件
                        String msg = MailUtil.sendResultEmains("找币",sy1+sy2,count,"BUY",succUsdt,thisMoney,allMoney,
                                HistoryUSDTList.get(HistoryUSDTList.size()-1)-HistoryUSDTList.get(0));
                        info(msg);
                    }

                    // 数据初始化
                    thisMoney = 0.0;
                    count = 0;
                    succUsdt=0.0;
                    // 重置邮件开关
                    emailStartMark = 0;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    // 有交易不释放锁
                }

            }
        }
    }


    public void info(String msg){
        logger.info("交易所: 找币, 交易对: " + sy1 + sy1 + "交易方式: BUY. \n " + msg);
    }

}
