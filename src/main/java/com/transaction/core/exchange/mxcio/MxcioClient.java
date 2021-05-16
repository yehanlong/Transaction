package com.transaction.core.exchange.mxcio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.AbstractExchange;
import com.transaction.core.utils.RestClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("抹茶Client")
public class MxcioClient extends AbstractExchange {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mocha.accessKey}")
    private String accessKey;
    @Value("${mocha.secretKey}")
    private String secretKey;

    RestClientWrapper clientWrapper = new RestClientWrapper(secretKey, accessKey);

    @Override
    public Map<String, PropertyVO> getAccount() {
        return null;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        PlaceOrderReq req = new PlaceOrderReq();
        //LIMIT_ORDER 限价订单 POST_ONLY 限价做市单 IMMEDIATE_OR_CANCEL 下单即撤销
        req.setOrderType("LIMIT_ORDER");
        String trade_type = ty.equals("BUY")?"BID":"ASK";
        req.setTradeType(trade_type);
//        req.setSymbol("ETH_USDT");
        req.setSymbol(currency+"_"+currency2);
        req.setPrice(String.valueOf(price));
        req.setQuantity(String.valueOf(amount));
        try {
            Result<String> result = clientWrapper.placeOrder(req);
            if(200 == result.getCode()) return true;
            else{
                logger.error("下单失败，result:{},req:{}",JSONObject.toJSONString(result),req);
                return false;
            }
        }catch(Exception e){
            logger.error("下单失败，error:{},req:{}",e,req);
            return false;
        }
    }

    @Override
    public TradeVO getMarketInfo(String sy1, String sy2) {
        String symbols = sy1 +"_"+ sy2;
        String url = "https://www.mxcio.co/api/platform/spot/market/deals?symbol=" + symbols;
        try {
            String result = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
            JSONObject object = JSON.parseObject(result);
            JSONObject jsonData = object.getJSONObject("data");
            JSONArray data = jsonData.getJSONArray("data");
            List<MxcioMarketInfoEntity> list = data.toJavaList(MxcioMarketInfoEntity.class);
            List<Order> byList = new ArrayList<>(), selList = new ArrayList<>();
            for (MxcioMarketInfoEntity entity : list) {
                Order order = new Order();
                order.setPrice(entity.getP());
                order.setAm(entity.getQ());
                //TODO 确认T值为1和2是买还是卖
                if(entity.getT().equals(1)){
                    byList.add(order);
                }else {
                    selList.add(order);
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
        return null;
    }

    @Override
    public double getSxf() {
        return 0;
    }
}
