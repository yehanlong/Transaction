package com.transaction.core.exchange.zhaobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pub.Symbol;
import com.transaction.core.exchange.pubinterface.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ZhaobiClient implements Exchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public PropertyVO getAccount(String a) {
       try {
           for (int i = 0; i < 10; i++) {
               PropertyVO p =  getAcc(a);
               double active = p.getActive();
               switch (a){
                   case "YCC":
                       if (active < 50.0) {
                           Thread.sleep(1000);
                           break;
                       }else {
                           return p;
                       }
                   case "BTY":
                       if (active < 3.0){
                           break;
                       }else {
                           return p;
                       }
               }

           }
       } catch (Exception e) {
           e.printStackTrace();
       }


       return getAcc(a);
    }

    private PropertyVO getAcc(String a){
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
        System.out.println("资产总数为："+valuation);
        JSONObject jsonList = jsonData.getJSONObject("list");
        Map<String, PropertyVO> map = new HashMap<>();
        Set<String> keySet = jsonList.keySet();
        for(String key:keySet){
            // 获得key
            JSONObject jsonBTY = jsonList.getJSONObject(key);
            PropertyVO propertyVO = JSON.parseObject(jsonBTY.toJSONString(), PropertyVO.class);
            map.put(key, propertyVO);
        }
        PropertyVO propertyVO = map.get(a);
        return propertyVO;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        String uri="https://api.biqianbao.top/api/trade/place";
        String requestText = //"amount=" + amount + "&" + "currency=" + currency + "&" + "currency2=" + currency2 + "&" + "price=" + price + "&" + "ty=" + ty;
        "amount="+amount+"&currency="+currency+"&currency2="+currency2+"&price="+price+"&ty="+ty;
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
        HttpEntity entity = new HttpEntity<>(requestText,headers);
        String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        return true;

    }

    @Override
    public TradeVO getMarketInfo(String symbols) {
        if(!Symbol.YCCUSDT.equals(symbols)&&!Symbol.BTYUSDT.equals(symbols)&&!Symbol.YCCBTY.equals(symbols)){
            logger.error("非法交易对");
        }
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
                if (i >= buyList.size() && i >= sellList.size())
                    break;
            }
            return new TradeVO(byList,selList);
        }catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }


    public static void main(String[] args) throws Exception {
        ZhaobiClient zhaobiClient = new ZhaobiClient();
        zhaobiClient.getAccount("YCC");
//        zhaobiClient.postBill(1,"YCC","USDT",0.013161,"SELL");
//        zhaobiClient.getMarketInfo("YCCUSDT");
    }
}
