package com.transaction.core.exchange.zt.handle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.zt.ZTCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnExpression("${zt.enabled:true}")
public class ZTHandleDepthMessage implements ZTHandleMessage {
    @Override
    public void handle(JSONObject json) {
        Integer id = json.getInteger("id");
        String symbol = ZTCache.depthSymbolMap.get(id);
        JSONObject result = json.getJSONObject("result");
        if(result == null){
            return;
        }
        TradeVO tradeVO = new TradeVO();
        JSONArray asksOrder = result.getJSONArray("asks");
        if(asksOrder != null && asksOrder.size() > 0){
            tradeVO.setSells(parseDepth(asksOrder));
        }
        JSONArray bidsOrder = result.getJSONArray("bids");
        if(bidsOrder != null && bidsOrder.size() > 0){
            tradeVO.setBuys(parseDepth(bidsOrder));
        }
        tradeVO.setSuccess(true);
        tradeVO.setTime(System.currentTimeMillis());
        ZTCache.orderMap.put(symbol,tradeVO);
        ZTCache.depthTradeMap.put(id,tradeVO);
    }

    @Override
    public boolean handleType(JSONObject json) {
        if(json == null){
            return false;
        }
        Integer id = json.getInteger("id");
        if(id == null){
            return false;
        }
        // depth.query主题订阅时，将(depth.query+symbol)的hashcode值作为id，并缓存到map中
        return ZTCache.depthSymbolMap.get(id) != null;
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
