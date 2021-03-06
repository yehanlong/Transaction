//package com.transaction.core.strategy;
//
//import com.transaction.core.constant.ZhaobiConstant;
//import com.transaction.core.entity.AmountPrice;
//import com.transaction.core.exchange.pub.RestTemplateStatic;
//import com.transaction.core.exchange.pubinterface.Exchange;
//import com.transaction.core.utils.DoubleUtil;
//import com.transaction.core.utils.MailUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//import java.text.DecimalFormat;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//
//// 例子：
//// 用比特元换ycc  sy1 bty   sy2 ycc
//// 1.买比特元 2.比特元换ycc 3.卖ycc
//// 注意：第二步的type是买
//
//public class SyncMoving1 extends Thread {
//
//
//    private Exchange client;
//    private String sy1;
//    private String sy2;
//    private Lock lock;
//
//    public Lock getLock() {
//        return lock;
//    }
//
//    public void setLock(Lock lock) {
//        this.lock = lock;
//    }
//
//    public Exchange getClient() {
//        return client;
//    }
//
//    public void setClient(Exchange client) {
//        this.client = client;
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
//    public SyncMoving1(Exchange client, String sy1, String sy2) {
//        this.client = client;
//        this.sy1 = sy1;
//        this.sy2 = sy2;
//    }
//    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public void run() {
//        logger.info("moving1 start, sy1: " + sy1 +", sy2: "+ sy2);
//
//        List<Double> HistoryUSDTList = new LinkedList<>();
//        double allMoney = 0.0;
//        int count = 0;
//        int in = 0;
//        int emailStartMark = 0;
//        double succUsdt = 0;
//        double accountMoney = 0.0;
//
//        // 初始usdt
////        ===
////        double startUSDT = client.getAccount().get("USDT").getActive();
////        HistoryUSDTList.add(startUSDT);
////        ===
//
//        while (true) {
//
//            try {
//                if (in ==0 ){
//                    lock.lock();
//                }
//
//                in = 0;
//
////        ===
////                double usdt = ZhaobiConstant.USDT;
//
////                double accUSDT = client.getAccount().get("USDT").getActive();
////                int a = DoubleUtil.compareTo(accUSDT,usdt);
////                if (a == -1) {
////                    logger.info("账户usdt 小于"+ZhaobiConstant.USDT);
////                    Thread.sleep(ZhaobiConstant.SLEEPTIME);
////                    continue;
////                }
////        ===
//
//                //ex:  s1 BTY  s2 YCC
//
//                // 异步获取市场行情 symbol1: BTY   symbol2:YCC
//                // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
//                FirstCacl t = new FirstCacl(client);
//                double usdtcount = t.getFirstCount(sy1,sy2,"BUY");
//                if (usdtcount == 0.0){
//                    logger.info("获取市场行情失败");
//                }
//                if (usdtcount > 4.97) {
//                    logger.info("预计一轮后的usdt：" + usdtcount);
//                }
//                AmountPrice amountPrice = t.getAmountPrice();
//
//                BigDecimal usdtcountB = new BigDecimal(usdtcount);
//                BigDecimal usdtB = new BigDecimal(5.0);
//                Double totalFee = ZhaobiConstant.TOTALFEE;
//                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
//                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(totalFee)))));
//                if (a3 == 1) {
//                    in = 1;
//                    // 有盈利，开始交易
//                    logger.info("有盈利，开始交易");
////        ===
////                    Map<String, PropertyVO> map = client.getAccount();
////                    logger.info("触发前USDT的余额为："+map.get("USDT").getValuation()+", 可用："+map.get("USDT").getActive()+", 冻结："+map.get("USDT").getFrozen());
////                    logger.info("触发前"+sy1+"的余额为："+map.get(sy1).getValuation()+", 可用："+map.get(sy1).getActive()+", 冻结："+map.get(sy1).getFrozen());
////                    logger.info("触发前"+sy2+"的余额为："+map.get(sy2).getValuation()+", 可用："+map.get(sy2).getActive()+", 冻结："+map.get(sy2).getFrozen());
////                    logger.info("触发前"+"USDT"+"的余额为："+map.get("USDT").getValuation()+", 可用："+map.get("USDT").getActive()+", 冻结："+map.get("USDT").getFrozen());
////
////                    if (accountMoney == 0.0) {
////                        accountMoney = map.get(sy1).getValuation() + map.get(sy2).getValuation() + map.get("USDT").getValuation();
////                    }
////        ===
//
//                    // 获取此轮交易实际需要的USDT
//                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice,  "BUY");
//
//                    logger.info("此次挂单可吃的usdt数量: " + ap.getMinUSDT());
//
//                    // 获取每次usdt
//                    double everyUSDT = 4.0;
//                    if(sy1 == "BTY" && sy2 == "YCC"){
//                        everyUSDT = Deal.getEveryUsdt(usdtcountB.doubleValue(),ap.getMinUSDT(),0.0,this.getName());
//                    }else {
//                        everyUSDT = Deal.getEveryUsdt(usdtcountB.doubleValue(),ap.getMinUSDT(),2.0,this.getName());
//                    }
//                    // 此处是为了保证吧小单吃完
//                    if (DoubleUtil.compareTo(ap.getMinUSDT() - everyUSDT, 1.5) == -1){
//                        everyUSDT = ap.getMinUSDT();
//                    }
//
//                    int a1 = DoubleUtil.compareTo(everyUSDT,ap.getMinUSDT());
//
//                    if (a1==1) {
//                        // 直接吃完整个订单
//                        // 一起执行3比交易
//                        succUsdt += ap.getMinUSDT();
//                        logger.info("直接吃完整个订单，吃单usdt数："+ ap.getMinUSDT());
//                        boolean b = client.syncPostBill(sy1, sy2, ap.getSy1Amount(), ap.getSy12Amount(), ap.getSy2Amount(), ap.getSy1Price(),
//                                ap.getSy12Price(), ap.getSy2Price(), "BUY");
//                        if(!b){
//                            logger.error("BUY or SELL 错误");
//                            return;
//                        }
//                    } else {
//                        // 如果4.0太少了，只能一步步吃
//                        // 一起执行3比交易
//
//                        //double point = everyUSDT / ap.getMinUSDT();
//                        double point = DoubleUtil.div(everyUSDT,ap.getMinUSDT(),25);
//
//                        // 处理btc的小数问题
//                        double am1 = DoubleUtil.mul(ap.getSy1Amount(), point);
//                        double point1 = 1.0;
//                        // 根据sy1来计算
//                        if (sy1=="BTC" && ap.getSy1Amount() > 0.0001){
//                            double dam1 = Double.valueOf(new DecimalFormat("0.0000").format(am1));
//                            point1 =  DoubleUtil.div(dam1,am1,25);
//                        }
//                        if (sy1=="ETH" && ap.getSy1Amount() > 0.001){
//                            double dam1 = Double.valueOf(new DecimalFormat("0.000").format(am1));
//                            point1 =  DoubleUtil.div(dam1,am1,25);
//                        }
//
//                        logger.info("一步步吃订单,吃单usdt数："+everyUSDT*point*point1);
//                        succUsdt += everyUSDT*point1;
//
//                        boolean b = client.syncPostBill(sy1, sy2, DoubleUtil.mulThree(ap.getSy1Amount(),point,point1),
//                                DoubleUtil.mulThree(ap.getSy12Amount(), point ,point1),
//                                DoubleUtil.mulThree(ap.getSy2Amount(),point, point1),
//                                ap.getSy1Price(), ap.getSy12Price(), ap.getSy2Price(), "BUY");
//                        if(!b){
//                            logger.error("BUY or SELL 错误");
//                            return;
//                        }
//                    }
//
//                    // 盈利统计，同时解决延迟问
////        ===
////                    double lastUSDT = HistoryUSDTList.get(HistoryUSDTList.size() - 1);
////                    double accUSDTEnd = client.getAccount().get("USDT").getActive();
////                    int a4 = DoubleUtil.compareTo(accUSDTEnd,accUSDT);
////        ===
//                    //int a5 = DoubleUtil.compareTo(DoubleUtil.sub(accUSDTEnd,accUSDT),-0.3);
//
//                    // 利用延迟时间， 在此处发邮件  或者数据库操作
//                    if (emailStartMark == 0) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                MailUtil.sendEmains("交易对"+sy1+sy2+" BUY触发, 预计的usdt为"
//                                        +usdtcountB.doubleValue()+", 预估此次可吃usdt为"+ap.getMinUSDT()+
//                                        "预估盈利RMB为："+(usdtcountB.doubleValue()-5.015) * ap.getMinUSDT()*7/5.0);
//                                logger.info("发送邮件");
//                            }
//                        }).start();
//
//                        emailStartMark = 1;
//                    }
//
//
//
//                    // 相等说明挂单的价格延迟，既挂单的时候没扣钱  <-1  指卖出的钱没到账
////                    for (int i= 0; i < 5; i++){
////                        if (a4 == 0) {
////                            // 此处需要保证不受延迟影响
////                            Thread.sleep(500);
////                            accUSDTEnd = client.getAccount().get("USDT").getActive();
////                            logger.info("获取余额延迟, 次数： " +  (i+1));
////                        }
////                    }
//
//                    count++;
//                    // 此次进入循环直到退出的盈利
//                    // 此交易对在程序运行期间的总盈利
////        ===
////                    allMoney = allMoney + DoubleUtil.sub(accUSDTEnd,lastUSDT);
////
////
////                    Map<String, PropertyVO> map1 = client.getAccount();
////
////                    logger.info("初始usdt： " + lastUSDT);
////                    logger.info("最终usdt： " + accUSDTEnd);
////                    logger.info("此次盈利USDT: " + DoubleUtil.sub(accUSDTEnd,lastUSDT));
////                    logger.info("USDT总盈利：" + DoubleUtil.sub(accUSDTEnd,HistoryUSDTList.get(0)));
////                    HistoryUSDTList.add(accUSDTEnd);
////        ===
//                    // 可以打印下历史数据HistoryUSDTList
//
//                }else {
////                    Thread.sleep(3000);
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (in == 0){
//                    lock.unlock();
//
//                    // 发送结果报告
//                    if (succUsdt != 0) {
//
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
////        ===
////                        Map<String, PropertyVO> map = client.getAccount();
////                        logger.info("最终"+sy1+"的余额为："+map.get(sy1).getValuation()+", 可用："+map.get(sy1).getActive()+", 冻结："+map.get(sy1).getFrozen());
////                        logger.info("最终"+sy2+"的余额为："+map.get(sy2).getValuation()+", 可用："+map.get(sy2).getActive()+", 冻结："+map.get(sy2).getFrozen());
////                        logger.info("最终"+"USDT"+"的余额为："+map.get("USDT").getValuation()+", 可用："+map.get("USDT").getActive()+", 冻结："+map.get("USDT").getFrozen());
////
////
////                        double end =  +map.get(sy1).getValuation() + map.get(sy2).getValuation() + map.get("USDT").getValuation();
////                        logger.info("初始金额：" + accountMoney + ", 最终金额：" + end + "， 盈利：" + (end-accountMoney));
////        ===
//
//                        // 发送最终结果邮件
//                        String msg = MailUtil.sendResultEmains(this.getName(),sy1+sy2,count,"BUY",succUsdt,0,allMoney,
//                                HistoryUSDTList.get(HistoryUSDTList.size()-1)-HistoryUSDTList.get(0));
//                        logger.info(msg);
//                    }
//
//                    // 数据初始化
//                    accountMoney = 0.0;
//                    count = 0;
//                    succUsdt=0.0;
//                    // 重置邮件开关
//                    emailStartMark = 0;
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }else {
//                    // 有交易不释放锁
//                }
//
//            }
//        }
//    }
//
//
//    public void info(String msg){
//        logger.info("找币,  " + sy1 + sy2 + ", 方式: BUY. " + msg);
//    }
//
//}
