package com.transaction.core.exchange.bidan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BiDanClient implements Exchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, PropertyVO> getAccount() {
        return null;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        return false;
    }
    @Override
    public TradeVO getMarketInfo(String symbols) {
        return new BiDanClient.MarketInnerClass(symbols).getMarketInfo(symbols);
    }

    // 获取订单信息的内部类
    class MarketInnerClass implements Runnable{

        private String symbols;

        private SyncMarkInfo syncMarkInfo;

        private int count;

        private CountDownLatch latch;

        public MarketInnerClass(String symbols){
            this.symbols = symbols;
        }

        public MarketInnerClass(String symbols, SyncMarkInfo syncMarkInfo, int count, CountDownLatch latch){
            this.symbols = symbols;
            this.syncMarkInfo = syncMarkInfo;
            this.count = count;
            this.latch = latch;
        }


        @Override
        public void run() {
            TradeVO tradeVO = getMarketInfo(symbols);
            syncMarkInfo.getLock().lock();
            if (count == 1) {
                this.syncMarkInfo.setTrade1(tradeVO);
            }else if (count == 2){
                this.syncMarkInfo.setTrade2(tradeVO);
            }else {
                this.syncMarkInfo.setTrade3(tradeVO);
            }
            syncMarkInfo.getLock().unlock();
            latch.countDown();
        }

        public TradeVO getMarketInfo(String symbols) {

            //

            String url = "https://market.coinegg.im/market/trade?symbol=" + symbols;
            try {
                String result = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
                JSONObject object = JSON.parseObject(result);
                JSONObject jsonData = object.getJSONObject("data");
                JSONObject marketData = jsonData.getJSONObject("marketdata");
                JSONArray buyList = marketData.getJSONArray("buy");
                JSONArray sellList = marketData.getJSONArray("sell");
                Order order = new Order();
                List<Order> byList = new ArrayList<>(), selList = new ArrayList<>();
                for (int i = 0; ; i++) {
                    if (i < buyList.size()) {
                        String buyInfo = JSONObject.toJSONString(buyList.get(i));
                        order = JSONObject.toJavaObject(JSONObject.parseObject(buyInfo), Order.class);
                        byList.add(order);
                    }
                    if (i < sellList.size()) {
                        String sellInfo = JSONObject.toJSONString(sellList.get(i));
                        order = JSONObject.toJavaObject(JSONObject.parseObject(sellInfo), Order.class);
                        selList.add(order);
                    }
                    if (i >= buyList.size() && i >= sellList.size()){
                        break;
                    }
                }
                return new TradeVO(byList,selList);
            }catch(Exception e){
                logger.error(e.getMessage());
                return null;
            }
        }
    }


    public SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2) {
        CountDownLatch latch = new CountDownLatch(3);
        Lock lock = new ReentrantLock();
        SyncMarkInfo syncMarkInfo = new SyncMarkInfo();
        syncMarkInfo.setLock(lock);
        BiDanClient.MarketInnerClass marketInnerClass1 = new BiDanClient.MarketInnerClass(symbol1.toLowerCase() + "_usdt", syncMarkInfo, 1, latch);
        BiDanClient.MarketInnerClass marketInnerClass2 = new BiDanClient.MarketInnerClass(symbol2.toLowerCase() + "_" + symbol1.toLowerCase(), syncMarkInfo, 2, latch);
        BiDanClient.MarketInnerClass marketInnerClass3 = new BiDanClient.MarketInnerClass(symbol2.toLowerCase() + "_usdt", syncMarkInfo, 3, latch);
        new Thread(marketInnerClass1).start();
        new Thread(marketInnerClass2).start();
        new Thread(marketInnerClass3).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return syncMarkInfo;
    }

    @Override
    public boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2, double amount3, double price1, double price2, double price3, String type) {
        return false;
    }
}
