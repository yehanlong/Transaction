package com.transaction.core.strategy;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.AmountPrice;
import com.transaction.core.exchange.pub.PostBill;
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
    private String type;
    // 交易策略  2代表一次次吃， 3代表同时吃单
    private int sType;


    public SyncDo(Exchange client, String sy1, String sy2, String sBase, Lock lock, String type) {
        this.client = client;
        this.sy1 = sy1;
        this.sy2 = sy2;
        this.sBase = sBase;
        this.lock = lock;
        this.type = type;
    }

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void doIt(){
        logger.info("start SyncDo, sy1: " + sy1 +", sy2: "+ sy2 + ", sBase: " + sBase);
        int count = 0;
        int in = 0;
        int emailStartMark = 0;
        double succUsdt = 0;

        if(type != "BUY" && type != "SELL"){
            logger.error("type err");
            return;
        }


        while (true) {

            try {
                if (in ==0 ){
                    lock.lock();
                }

                in = 0;

                // 异步获取市场行情 symbol1: BTY   symbol2:YCC   sBase:USDT
                // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
                FirstCacl t = new FirstCacl(client);
                double usdtcount = t.getFirstCount(sy1,sy2,sBase,type);
                if (usdtcount == 0.0){
                    logger.info("获取市场行情失败");
                    Thread.sleep(5000);
                }
                if (usdtcount > client.showlogPrice()) {
                    logger.info("预计一轮后的usdt：" + usdtcount);
                }
                AmountPrice amountPrice = t.getAmountPrice();
                BigDecimal usdtcountB = new BigDecimal(usdtcount);
                BigDecimal usdtB = new BigDecimal(5.0);
                Double totalFee = client.getStartPercentage();
                // 判断一轮交易后的去掉手续费（3次=3*0.001），是否有盈利
                int a3 = usdtcountB.compareTo(usdtB.multiply(((new BigDecimal(1)).add(new BigDecimal(totalFee)))));
                if (a3 == 1) {
                    in = 1;
                    // 有盈利，开始交易
                    logger.info("有盈利，开始交易...");
                    // 获取此轮交易实际需要的USDT
                    AmountPrice ap = Deal.getAcuallyUSDT(amountPrice,  type, client.getSxf());
                    if (ap.getMinUSDT() < PubConst.getMin(sBase)){
                        continue;
                    }

                    logger.info("此次挂单可吃的usdt数量: " + ap.getMinUSDT());

                    // 获取每次usdt
                    double everyUSDT = client.getEveryUSDT(sy1,sy2,sBase);

                    int a1 = DoubleUtil.compareTo(everyUSDT,ap.getMinUSDT());
                    double point = 1.0;
                    double point1 = 1.0;
                    if (a1 == 1) {
                        // 小数位的问题  不一定能吃完
                         logger.info("直接吃完整个订单");
                    }else {
                        logger.info("一步步吃订单");
                        point = DoubleUtil.div(everyUSDT,ap.getMinUSDT(),10);
                    }
                    // todo 处理舍弃小数位的问题
                    // 一般价格高的是sy1  此次需要注意，可能有的交易所不是这样的
                    String smallCount = client.getSmallCount(sBase,sy1);
                    while (true) {
                        double top = Double.valueOf(new DecimalFormat(smallCount).format( ap.getSy1Amount()));
                        point1 = DoubleUtil.div(top,ap.getSy1Amount(),10);
                        if (everyUSDT * point1 < PubConst.getMin(sBase)){
                            everyUSDT = everyUSDT + 2;
                            continue;
                        }
                        break;
                    }

                    // 判断账户余额  当账户余额太小，不进行交易
                    if (client.getAccount().get(sBase).getActive() < everyUSDT){
                        Thread.sleep(5000);
                        continue;
                    }


                    // 一起执行3比交易
                    succUsdt += ap.getMinUSDT();
                    logger.info("此次吃单"+sBase+"数："+ ap.getMinUSDT() *point*point1 );
                    if (sType == 2) {
                        boolean b = PostBill.postBill(client,sy1, sy2,sBase, ap.getSy1Amount()*point*point1, ap.getSy12Amount()*point*point1, ap.getSy2Amount()*point*point1, ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), type);
                        if(!b){
                            logger.error("挂单 错误");
                            return;
                        }
                    }
                    if (sType == 3){
                        logger.info("市场行情：{}", JSONObject.toJSONString(amountPrice));
                        boolean b = PostBill.syncPostBill(client,sy1, sy2,sBase, ap.getSy1Amount()*point*point1, ap.getSy12Amount()*point*point1, ap.getSy2Amount()*point*point1, ap.getSy1Price(),
                                ap.getSy12Price(), ap.getSy2Price(), type);
                        if(!b){
                            logger.error("挂单 错误");
                            return;
                        }
                    }


                    // 利用延迟时间， 在此处发邮件  或者数据库操作
                    if (emailStartMark == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MailUtil.sendEmains("交易对"+sy1 + sy2 + sBase + type + " 触发, 预计的usdt为"
                                        +usdtcountB.doubleValue()+", 预估此次可吃usdt为:"+ap.getMinUSDT()+
                                        "预估盈利RMB为："+(usdtcountB.doubleValue()-5.015) * ap.getMinUSDT()*7/5.0);
                                logger.info("发送邮件");
                            }
                        }).start();

                        emailStartMark = 1;
                    }
                    count++;
//                    // todo  没问题之后去掉这个
//                    Thread.sleep(10000);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in == 0){
                    lock.unlock();

                    // 发送结果报告
                    if (succUsdt != 0) {
                        // 发送最终结果邮件
                        String msg = MailUtil.sendResultEmains(client.getName(),sy1+sy2+sBase,count,type,succUsdt);
                        logger.info(msg);
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
