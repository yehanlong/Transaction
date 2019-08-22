package com.transaction.core.strategy;

import com.transaction.core.entity.AmountPrice;
import com.transaction.core.exchange.pub.PubConst;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.utils.DoubleUtil;
import com.transaction.core.utils.MailUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.locks.Lock;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncDo {
    private Exchange client;
    private String sy1;
    private String sy2;
    private String sBase;
    private Lock lock;

    public SyncDo(Exchange client, String sy1, String sy2) {
        this.client = client;
        this.sy1 = sy1;
        this.sy2 = sy2;
    }
    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void buy(){
        logger.info("start, sy1: " + sy1 +", sy2: "+ sy2);
        int count = 0;
        int in = 0;
        int emailStartMark = 0;
        double succUsdt = 0;


        while (true) {

            try {
                if (in ==0 ){
                    lock.lock();
                }

                in = 0;

                // 异步获取市场行情 symbol1: BTY   symbol2:YCC   sBase:USDT
                // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
                FirstCacl t = new FirstCacl(client);
                double usdtcount = t.getFirstCount(sy1,sy2,sBase,"BUY");
                if (usdtcount == 0.0){
                    logger.info("获取市场行情失败");
                }
                if (usdtcount > client.showlogPrice()) {
                    logger.info("预计一轮后的usdt：" + usdtcount);
                }
                AmountPrice amountPrice = t.getAmountPrice();

                BigDecimal usdtcountB = new BigDecimal(usdtcount);
                BigDecimal usdtB = new BigDecimal(5.0);
                Double totalFee = client.getStartPrecentage();
                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(totalFee)))));
                if (a3 == 1) {
                    in = 1;
                    // 有盈利，开始交易
                    logger.info("有盈利，开始交易...");

                    // 获取此轮交易实际需要的USDT
                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice,  "BUY");

                    logger.info("此次挂单可吃的usdt数量: " + ap.getMinUSDT());

                    // 获取每次usdt
                    double everyUSDT = client.getEveryUSDT();

                    // 此处是为了保证吧小单吃完
                    if (DoubleUtil.compareTo(ap.getMinUSDT() - everyUSDT, PubConst.minUSDT) == -1){
                        everyUSDT = ap.getMinUSDT();
                    }

                    int a1 = DoubleUtil.compareTo(everyUSDT,ap.getMinUSDT());

                    if (a1==1) {
                        // 直接吃完整个订单
                        // 一起执行3比交易
                        succUsdt += ap.getMinUSDT();
                        logger.info("直接吃完整个订单，吃单usdt数："+ ap.getMinUSDT());
                        boolean b = client.syncPostBill(sy1, sy2,sBase, ap.getSy1Amount(), ap.getSy12Amount(), ap.getSy2Amount(), ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                            return;
                        }
                    } else {
                        // 如果4.0太少了，只能一步步吃
                        // 一起执行3比交易

                        //double point = everyUSDT / ap.getMinUSDT();
                        double point = DoubleUtil.div(everyUSDT,ap.getMinUSDT(),25);

                        // 此处需要优化
                        // 处理btc的小数问题
                        double am1 = DoubleUtil.mul(ap.getSy1Amount(), point);
                        double point1 = 1.0;
                        // 根据sy1来计算
                        if (sy1=="BTC" && ap.getSy1Amount() > 0.0001){
                            double dam1 = Double.valueOf(new DecimalFormat("0.0000").format(am1));
                            point1 =  DoubleUtil.div(dam1,am1,25);
                        }
                        if (sy1=="ETH" && ap.getSy1Amount() > 0.001){
                            double dam1 = Double.valueOf(new DecimalFormat("0.000").format(am1));
                            point1 =  DoubleUtil.div(dam1,am1,25);
                        }

                        if (everyUSDT < 1.5){
                            logger.info("usdt太少:"+ everyUSDT);
                            continue;
                        }

                        logger.info("一步步吃订单,吃单usdt数："+everyUSDT*point*point1);
                        succUsdt += everyUSDT*point1;

                        boolean b = client.syncPostBill(sy1, sy2,sBase, DoubleUtil.mulThree(ap.getSy1Amount(),point,point1),
                                DoubleUtil.mulThree(ap.getSy12Amount(), point ,point1),
                                DoubleUtil.mulThree(ap.getSy2Amount(),point, point1),
                                ap.getSy1Price(), ap.getSy12Price(), ap.getSy2Price(), "BUY");
                        if(!b){
                            logger.error("BUY or SELL 错误");
                            return;
                        }
                    }


                    // 利用延迟时间， 在此处发邮件  或者数据库操作
                    if (emailStartMark == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MailUtil.sendEmains("交易对"+sy1 + sy2 + sBase +" BUY触发, 预计的usdt为"
                                        +usdtcountB.doubleValue()+", 预估此次可吃usdt为:"+ap.getMinUSDT()+
                                        "预估盈利RMB为："+(usdtcountB.doubleValue()-5.015) * ap.getMinUSDT()*7/5.0);
                                logger.info("发送邮件");
                            }
                        }).start();

                        emailStartMark = 1;
                    }


                    count++;

                }else {
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in == 0){
                    lock.unlock();

                    // 发送结果报告
                    if (succUsdt != 0) {
                        // 发送最终结果邮件
                        //String msg = MailUtil.sendResultEmains(client.getName(),sy1+sy2,count,"BUY",succUsdt,);
                        //logger.info(msg);
                    }

                    // 数据初始化

                    count = 0;
                    succUsdt=0.0;
                    // 重置邮件开关
                    emailStartMark = 0;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else {
                    // 有交易不释放锁
                }

            }
        }
    }

}
