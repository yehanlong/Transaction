package com.transaction.core.exchange.bidan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.AbstractExchange;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service("币蛋Client")
public class BiDanClient extends AbstractExchange {

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
    public TradeVO getMarketInfo(String sy1, String sy2) {
        String symbol = sy2+"_"+sy1;
        return new BiDanClient.MarketInnerClass(symbol).getMarketInfo(symbol);
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

            String url = "https://trade.coinegg.im/web/data/depth?symbol=" + symbols+"&"+System.currentTimeMillis();
            try {
                String result = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
                JSONObject object = JSON.parseObject(result);

                List<Order> sellList = parseDepth(object.getJSONArray("asks"));
                List<Order> buyList = parseDepth(object.getJSONArray("bids"));
                Collections.sort(sellList);
                Collections.sort(buyList);
                List<Order> buys = new ArrayList<>();
                buyList.forEach((order)->{buys.add(0,order);});
                return new TradeVO(buys,sellList);
            }catch(Exception e){
                logger.error(e.getMessage());
                return null;
            }
        }

        private List<Order> parseDepth(JSONArray array){
            List<Order> orders = new ArrayList<>();
            for(int i=0;i<array.size();i++){
                JSONArray ask = array.getJSONArray(i);
                Order order = new Order();
                order.setPrice(ask.getDouble(0));
                order.setAm(ask.getDouble(1));
                orders.add(order);
            }
            return orders;
        }
    }

    @Override
    public String getName() {
        return "币蛋";
    }

    @Override
    public double getSxf() {
        return 0.001;
    }

}
