package com.transaction.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.Symbol;
import com.transaction.core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 账户serviceimpl
 * @author: yhl
 * @create: 2019-08-05
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map getAccount() throws IOException {
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
        return map;
    }

    @Override
    public List<TradeVO> getZBMarketInfo(int num, String symbol) throws Exception {
        if(!Symbol.YCCUSDT.equals(symbol)&&!Symbol.BTYUSDT.equals(symbol)&&!Symbol.YCCBTY.equals(symbol)){
            throw new Exception("非法交易对");
        }
        String url = "https://api.biqianbao.top/api/data/market?num=" + String.valueOf(num) + "&format=&symbol=" + symbol;
        String result =restTemplate.exchange(url, HttpMethod.GET , null, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        JSONObject jsonData = object.getJSONObject("data");
        JSONArray jsonTrade = jsonData.getJSONArray("trade");
        List<TradeVO> list = JSONObject.parseArray(jsonTrade.toJSONString(), TradeVO.class);
        return list;
    }
}
