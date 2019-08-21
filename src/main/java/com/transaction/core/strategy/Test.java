package com.transaction.core.strategy;


import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

// 测试的策略
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test implements Strategy{
    private Exchange client;
    private String sy1;
    private String sy2;
    private String sBase;


    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start(Lock lock, Exchange client, String sy1, String sy2, String SBase) {

        FirstCacl buy = new FirstCacl(client);

        new Thread(()->{
            while (true){
                try {
                    double usdtcount = buy.getFirstCount(sy1,sy2,sBase,"BUY");
                    if (usdtcount == 0.0){
                        logger.info("获取市场行情失败");
                    }
                    if(usdtcount > client.showlogPrice()){
                        logger.info("预计一轮后的usdt：" + usdtcount);
                    }

                }catch (Exception e){
                    logger.error("计算交易出错",sy1,sy2);
                }

                try {
                    TimeUnit.SECONDS.sleep(client.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        FirstCacl sell = new FirstCacl(client);

        new Thread(()->{
            while (true){
                try {
                    double usdtcount = sell.getFirstCount(sy1,sy2,sBase,"SELL");
                    if (usdtcount == 0.0){
                        logger.info("获取市场行情失败");
                    }
                    if(usdtcount > client.showlogPrice()){
                        logger.info("预计一轮后的usdt：" + usdtcount);
                    }

                }catch (Exception e){
                    logger.error("计算交易出错",sy1,sy2);
                }

                try {
                    TimeUnit.SECONDS.sleep(client.getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
