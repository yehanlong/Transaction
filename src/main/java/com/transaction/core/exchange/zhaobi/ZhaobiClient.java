package com.transaction.core.exchange.zhaobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pub.Symbol;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.utils.FontUtil;
import com.transaction.core.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZhaobiClient implements Exchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Map<String, PropertyVO> getAccount() {
//       try {
//           for (int i = 0; i < 10; i++) {
//               PropertyVO p =  getAcc(a);
//               double active = p.getActive();
//               switch (a){
//                   case "YCC":
//                       if (active < 50.0) {
//                           Thread.sleep(1000);
//                           break;
//                       }else {
//                           return p;
//                       }
//                   case "BTY":
//                       if (active < 3.0){
//                           break;
//                       }else {
//                           return p;
//                       }
//                   case "USDT":
//                       if (active < 1.5) {
//                           break;
//                       }else {
//                           return p;
//                       }
//               }

//           }
//       } catch (Exception e) {
//           e.printStackTrace();
//       }


       return getAcc();
    }

    private Map<String, PropertyVO> getAcc(){
        String uri="https://api.biqianbao.top/api/Account/Asset";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.add("FZM-REQUEST-OS", "FZM-REQUEST-OS");
        headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
        //headers.add("Authorization ", "Bearer e243dff6f8132ef254fae4e1f628e6d7966f8645");
        HttpEntity entity = new HttpEntity<>(headers);
        String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        JSONObject jsonData = object.getJSONObject("data");
        String valuation = jsonData.getString("valuation");
        //System.out.println("资产总数为："+valuation);
        JSONObject jsonList = jsonData.getJSONObject("list");
        Map<String, PropertyVO> map = new HashMap<>();
        Set<String> keySet = jsonList.keySet();
        for(String key:keySet){
            // 获得key
            JSONObject jsonBTY = jsonList.getJSONObject(key);
            PropertyVO propertyVO = JSON.parseObject(jsonBTY.toJSONString(), PropertyVO.class);
            map.put(key, propertyVO);
        }
        //PropertyVO propertyVO = map.get(a);
        return map;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
//
        return new PlaceOrderInnerClass().postBill(amount, currency, currency2, price, ty);
    }

    @Override
    public TradeVO getMarketInfo(String symbols) {
        return new MarketInnerClass(symbols).getMarketInfo(symbols);
    }


    // 异步获取市场行情
    @Override
    public SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2){
        CountDownLatch latch = new CountDownLatch(3);
        Lock lock = new ReentrantLock();
        SyncMarkInfo syncMarkInfo = new SyncMarkInfo();
        syncMarkInfo.setLock(lock);
        MarketInnerClass marketInnerClass1 = new MarketInnerClass(symbol1+"USDT",syncMarkInfo,1,latch);
        MarketInnerClass marketInnerClass2 = new MarketInnerClass(symbol2+symbol1,syncMarkInfo,2,latch);
        MarketInnerClass marketInnerClass3 = new MarketInnerClass(symbol2+"USDT",syncMarkInfo,3,latch);
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
//            if(!Symbol.YCCUSDT.equals(symbols)&&!Symbol.BTYUSDT.equals(symbols)&&!Symbol.YCCBTY.equals(symbols)){
//                logger.error("非法交易对：",symbols);
//            }
            String url = "https://api.biqianbao.top/api/data/market?num=" + String.valueOf(10) + "&format=&symbol=" + symbols;
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


    // 挂单内部类
    class PlaceOrderInnerClass implements Runnable{

        private double amount;
        private String currency;
        private String currency2;
        private double price;
        private String ty;
        private CountDownLatch latch;

        public PlaceOrderInnerClass(){}

        public PlaceOrderInnerClass(double amount, String currency, String currency2, double price, String ty, CountDownLatch latch){
            this.amount = amount;
            this.currency = currency;
            this.currency2 = currency2;
            this.price = price;
            this.ty = ty;
            this.latch = latch;
        }
        @Override
        public void run() {
            postBill(amount,currency,currency2,price,ty);
            latch.countDown();
        }

        public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
//
            String amountStr = Deal.dealCount(amount,currency);
            String priceStr = Deal.dealPrice(price,currency2);
            String uri="https://api.biqianbao.top/api/trade/place";
            String requestText = //"amount=" + amount + "&" + "currency=" + currency + "&" + "currency2=" + currency2 + "&" + "price=" + price + "&" + "ty=" + ty;
                    "amount="+amountStr+"&currency="+currency+"&currency2="+currency2+"&price="+priceStr+"&ty="+ty;
            HttpHeaders headers = new HttpHeaders();
            //定义请求参数类型
            headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
            headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
            HttpEntity entity = new HttpEntity<>(requestText,headers);
            String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
            logger.info(result);
            JSONObject object = JSON.parseObject(result);
            String code = object.getString("code");
            if(!"200".equals(code)){
                String message = FontUtil.decodeUnicode(object.getString("message"));
                MailUtil.sendEmains("挂单操作失败"+message);
            }
            return true;

        }

    }


    // 异步挂单
    // type指第二步买还是卖
    // symbol1 永远都是bty
    @Override
    public boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2, double amount3,
                                double price1, double price12, double price2, String type) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(3);

        if(type=="BUY"){
            // 买bty   bty买ycc  卖ycc
            PlaceOrderInnerClass t1 = new PlaceOrderInnerClass(amount1, symbol1,"USDT", price1, "BUY", latch);
            PlaceOrderInnerClass t2 = new PlaceOrderInnerClass(amount2, symbol2,symbol1, price12, type, latch);
            PlaceOrderInnerClass t3 = new PlaceOrderInnerClass(amount3, symbol2,"USDT", price2, "SELL", latch);
            new Thread(t1).start();
            new Thread(t2).start();
            new Thread(t3).start();
            latch.wait();
            return true;
        }

        if(type=="SELL"){
            //  买ycc  卖ycc换bty 卖bty
            PlaceOrderInnerClass t1 = new PlaceOrderInnerClass(amount1, symbol1,"USDT", price1, "SELL", latch);
            PlaceOrderInnerClass t2 = new PlaceOrderInnerClass(amount2, symbol2,symbol1, price12, type, latch);
            PlaceOrderInnerClass t3 = new PlaceOrderInnerClass(amount3, symbol2,"USDT", price2, "BUY", latch);
            new Thread(t1).start();
            new Thread(t2).start();
            new Thread(t3).start();
            latch.wait();
            return true;
        }


        return false;
    }



    public static void main(String[] args) throws Exception {
        ZhaobiClient zhaobiClient = new ZhaobiClient();
        zhaobiClient.getAccount().get("YCC");
//        zhaobiClient.postBill(1,"YCC","USDT",0.013161,"SELL");
//        zhaobiClient.getMarketInfo("YCCUSDT");
    }
}
