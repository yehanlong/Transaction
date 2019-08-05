package com.transaction.core.exchange.zhaobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Account;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pub.Symbol;
import com.transaction.core.exchange.pubinterface.Exchange;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZhaobiClient implements Exchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();

    @Override
    public PropertyVO getAccount(String a) {
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
    public TradeVO getMarketInfo(String symbols) throws Exception {
        if(!Symbol.YCCUSDT.equals(symbols)&&!Symbol.BTYUSDT.equals(symbols)&&!Symbol.YCCBTY.equals(symbols)){
            throw new Exception("非法交易对");
        }
        String url = "https://api.biqianbao.top/api/data/market?num=" + String.valueOf(10) + "&format=&symbol=" + symbols;
        String result =restTemplate.exchange(url, HttpMethod.GET , null, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        JSONObject jsonData = object.getJSONObject("data");
        JSONArray jsonTrade = jsonData.getJSONArray("trade");
        List<TradeVO> list = JSONObject.parseArray(jsonTrade.toJSONString(), TradeVO.class);
        return null;
    }


    public static void main(String[] args) throws Exception {
        ZhaobiClient zhaobiClient = new ZhaobiClient();
        //zhaobiClient.getAccount("YCC");
        //zhaobiClient.postBill(1,"YCC","USDT",0.013161,"SELL");
        zhaobiClient.getMarketInfo("YCCUSDT");
    }
}
