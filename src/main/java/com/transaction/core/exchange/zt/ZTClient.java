package com.transaction.core.exchange.zt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.AbstractExchange;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.utils.SpringUtil;
import com.transaction.core.ws.WebSocketClient;
import com.transaction.core.ws.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service("ZTClient")
@Slf4j
public class ZTClient extends AbstractExchange {
    @Override
    public Map<String, PropertyVO> getAccount() {
        RestTemplate restTemplate = RestTemplateStatic.restTemplate();
        String uri = "https://www.zt.com/api/v1/user/assets";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (ZTCache.token == null) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                throw new Exception("token异常");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        headers.add("Authorization", ZTCache.token);
        HttpEntity entity = new HttpEntity<>(headers);
        String json = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(json);
        JSONObject result = object.getJSONObject("result");
        Map<String, PropertyVO> map = new HashMap<>();
        Set<String> keySet = result.keySet();
        for (String key : keySet) {
            // 获得key
            JSONObject jsonCurrency = result.getJSONObject(key);
            PropertyVO propertyVO = PropertyVO.builder()
                    .active(Double.valueOf(jsonCurrency.getString("available")))
                    .frozen(Double.valueOf(jsonCurrency.getString("freeze")))
                    .name(key)
                    .build();
            map.put(key, propertyVO);
        }
        return map;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        String side = "";

        if (ZTCache.token == null) {
            System.out.println(ZTCache.token);
            return false;
        } else {
            System.out.println(ZTCache.token);
        }

        if (ty == "BUY") {
            side = "2";
        } else if (ty == "SELL") {
            side = "1";
        } else {
            System.out.println(currency + currency2 + "ty false");
            return false;
        }

        return postBillZT(amount, currency + "_" + currency2, side, price, ZTCache.token);
    }

    @Override
    public TradeVO getMarketInfo(String sy1, String sy2) {
        WebSocketClient webSocketClient = (WebSocketClient) SpringUtil.getBean("ztWebSocketClient");
        while (!webSocketClient.getConnected()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String subject = "{\"method\":\"depth.query\",\"params\":[\"%s\",10,\"0.00000001\"],\"id\":%d}";
        int id = UUID.randomUUID().toString().hashCode();
        ZTCache.depthSymbolMap.put(id,sy2+"_"+sy1);
        webSocketClient.send(String.format(subject,sy2+"_"+sy1,id));
        TradeVO vo;
        while ((vo = ZTCache.depthTradeMap.get(id))==null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ZTCache.depthTradeMap.remove(id);
        ZTCache.depthSymbolMap.remove(id);
        return vo;
    }

    public boolean postBillZT(double amount, String market, String side, double price, String token) {
        System.out.println(market + " ：amount：" + amount + ", price: " + price + ", side:" + side);
        RestTemplate restTemplate = RestTemplateStatic.restTemplate();
        String uri = "https://www.zt.com/api/v1/user/trade/limit";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", token);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("market", market);
        map.add("side", side);
        String amountStr = new DecimalFormat(getSmallCount(market.split("_")[1], market.split("_")[0])).format(amount);
        map.add("amount", amountStr);
        map.add("price", String.valueOf(price));
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        String result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        log.info(object.toJSONString() + "------"+JSONObject.toJSONString(map));
        return false;
    }

    public static void main(String[] args) {
        ZTCache.token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySWQiOjExNTcxNDQsIkxvZ2luVmVyaWZ5IjoxLCJleHAiOjE1NjY3MDgzMTN9.ygDhtvGGprEmIZFwNSqd2X_MufP09TrBHYfdh_ZONgE";
//        postBillZT(0.01,"EOS_CNT","2",20.1526,
//                ZTCache.token);
        ZTClient ztClient = new ZTClient();
        ztClient.getAccount();
    }

    @Override
    public String getName() {
        return "ZT";
    }

    @Override
    public double getSxf() {
        return 0;
    }

    public void init(String platform, List<SymbolConfig> symbolConfigs) {
        WebSocketService webSocketService = (WebSocketService) SpringUtil.getBean("ztWebSocketService");
        webSocketService.init(symbolConfigs);
    }

    private class InnerTradeThread implements Runnable{

        private CountDownLatch latch;
        private SyncMarkInfo info;
        private int type;
        private int id;

        public InnerTradeThread(CountDownLatch latch, SyncMarkInfo info, int type, int id) {
            this.latch = latch;
            this.info = info;
            this.type = type;
            this.id = id;
        }

        @Override
        public void run() {
            TradeVO vo;
            while ((vo = ZTCache.depthTradeMap.get(id))==null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(type == 1){
                info.setTrade1(vo);
            }else if (type == 2){
                info.setTrade2(vo);
            }else{
                info.setTrade3(vo);
            }
            ZTCache.depthTradeMap.remove(id);
            ZTCache.depthSymbolMap.remove(id);
            latch.countDown();
        }
    }
}
