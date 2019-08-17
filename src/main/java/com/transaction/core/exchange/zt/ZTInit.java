package com.transaction.core.exchange.zt;

import com.transaction.core.utils.SpringUtil;
import com.transaction.core.ws.WebSocketService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ZTInit {

    public Map<String,List<String>> initSymbol() {
        Map<String,List<String>> symbolMap = new HashMap<>();
        List<String> btc = new LinkedList<>();
        btc.add("EOS");
        btc.add("ETH");
        btc.add("LTC");
        symbolMap.put("BTC",btc);
        List<String> eth = new LinkedList<>();
        eth.add("EOS");
        symbolMap.put("ETH",eth);
        // spring初始化webSocket
        WebSocketService webSocketService = (WebSocketService) SpringUtil.getBean("ztWebSocketService");
        webSocketService.init(symbolMap);
        return symbolMap;

    }
}
