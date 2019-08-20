package com.transaction.core.exchange.zt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZTClient implements Exchange{
    @Override
    public Map<String, PropertyVO> getAccount() {
        return null;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        return false;
    }


    public static boolean postBillZT(double amount, String market, String side, double price, String token) {
        RestTemplate restTemplate = RestTemplateStatic.restTemplate();
        String uri="https://www.zt.com/api/v1/user/trade/limit";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization","Bearer "+token);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("market",market);
        map.add("side",side);
        map.add("amount",String.valueOf(amount));
        map.add("price",String.valueOf(price));
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,headers);
        String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        System.out.println(object.toJSONString());
        return false;
    }

    public static void main(String[] args) {
        ZTCache.token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySWQiOjExNTcxNDQsIkxvZ2luVmVyaWZ5IjoxLCJleHAiOjE1NjYzNDIxMTF9.MIbOqSOFCtKC97x4CayfaFWf4Pq1ID9eGpY5hTR5eyk";
        postBillZT(0.01,"EOS_CNT","2",20.1526,
                ZTCache.token);
    }

    @Override
    public TradeVO getMarketInfo(String symbol) {
        TradeVO vo = ZTCache.orderMap.get(symbol);
        return vo;
    }

    @Override
    public SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2) {
        SyncMarkInfo info = new SyncMarkInfo();
        String s1USDT = symbol1 + "_" + "USDT";
        String s2s1 = symbol2 + "_" + "USDT";
        String s2USDT = symbol2 + "_" + symbol1;
        while (true){
            TradeVO s1Trade = ZTCache.orderMap.get(s1USDT);
            TradeVO s2Trade = ZTCache.orderMap.get(s2USDT);
            TradeVO s2s1Trade = ZTCache.orderMap.get(s2s1);
            long currentTime = System.currentTimeMillis();
            if(s1Trade == null || s2Trade == null || s2s1Trade == null){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if(currentTime-s1Trade.getTime() > 1000
                    || currentTime - s2Trade.getTime() > 1000
                    || currentTime-s2s1Trade.getTime() > 1000){
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            info.setTrade1(s1Trade);
            info.setTrade2(s2Trade);
            info.setTrade3(s2s1Trade);
            return info;
        }

    }

    @Override
    public boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2, double amount3, double price1, double price2, double price3, String type) {
        return false;
    }

    @Override
    public String getName() {
        return "ZT";
    }
}
