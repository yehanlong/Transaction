package com.transaction.core.exchange.zhaobi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.AbstractExchange;
import com.transaction.core.utils.FontUtil;
import com.transaction.core.utils.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.*;

@Service("找币Client")
public class ZhaobiClient extends AbstractExchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${zhaobi.token}")
    private String token;

    @Override
    public Map<String, PropertyVO> getAccount() {
       return getAcc();
    }

    private Map<String, PropertyVO> getAcc(){
        String uri="https://api.biqianbao.top/api/Account/Asset";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.add("FZM-REQUEST-OS", "FZM-REQUEST-OS");
        headers.setBearerAuth(token);
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
        return map;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        // todo 此次改成交易所自己维护价格处理
        String zero = getSmallCount(currency2,currency);
        String amountStr = new DecimalFormat(zero).format(amount);
        String priceStr = String.valueOf(price);
        String uri="https://api.biqianbao.top/api/trade/place";
        String requestText = //"amount=" + amount + "&" + "currency=" + currency + "&" + "currency2=" + currency2 + "&" + "price=" + price + "&" + "ty=" + ty;
                "amount="+amountStr+"&currency="+currency+"&currency2="+currency2+"&price="+priceStr+"&ty="+ty;
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity<>(requestText,headers);
        String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
        logger.info("挂单：{}----{}",requestText,result);
        JSONObject object = JSON.parseObject(result);
        String code = object.getString("code");
        if(!"200".equals(code)){
            String message = FontUtil.decodeUnicode(object.getString("message"));
            MailUtil.sendEmains("挂单操作失败，"+message+"，url："+requestText);
        }
        return true;

//        return new PlaceOrderInnerClass().postBill(amount, currency, currency2, price, ty);
    }

    @Override
    public TradeVO getMarketInfo(String sy1, String sy2) {
        String symbols = sy1 + sy2;
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




    @Override
    public String getName() {
        return "找币";
    }

    @Override
    public double getSxf() {
        return 0.001;
    }


    public static void main(String[] args) throws Exception {
        ZhaobiClient zhaobiClient = new ZhaobiClient();
        zhaobiClient.getAccount().get("YCC");
    }
}
