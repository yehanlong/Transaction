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
        btc.add("DASH");
        btc.add("ETC");
        btc.add("ETH");
        btc.add("LTC");
        btc.add("NEO");
        btc.add("UPA");
        btc.add("VOLLAR");
        btc.add("XRP");
        btc.add("EOS");
        symbolMap.put("BTC",btc);
        List<String> eth = new LinkedList<>();
        eth.add("EOS");
        eth.add("KGC");
        eth.add("UPA");
        symbolMap.put("ETH",eth);
//        List<String> cnt = new LinkedList<>();
//        cnt.add("ANY");
//        cnt.add("BALIC");
//        cnt.add("BTC");
//        cnt.add("DASH");
//        cnt.add("EOS");
//        cnt.add("ETC");
//        cnt.add("ETH");
//        cnt.add("GHP");
//        cnt.add("GUBI");
//        cnt.add("LTC");
//        cnt.add("NEO");
//        cnt.add("SHELL");
//        cnt.add("TRX");
//        cnt.add("UPA");
//        cnt.add("VOLLAR");
//        cnt.add("XRP");
//        symbolMap.put("CNT",cnt);
        // spring初始化webSocket
        WebSocketService webSocketService = (WebSocketService) SpringUtil.getBean("ztWebSocketService");
        webSocketService.init(symbolMap);
        return symbolMap;

    }


    public Map<String,List<String>> initCNTSymbol() {
        Map<String,List<String>> symbolMap = new HashMap<>();
        List<String> usdt = new LinkedList<>();
//        usdt.add("ANY");
//        usdt.add("BALIC");
//        usdt.add("BTC");
//        usdt.add("DASH");
//        usdt.add("EOS");
//        usdt.add("ETH");
//        usdt.add("ETC");
//        usdt.add("GHP");
//        usdt.add("GUBI");
//        usdt.add("LTC");
//        usdt.add("NEO");
//        usdt.add("SHELL");
//        usdt.add("TRX");
//        usdt.add("UPA");
        usdt.add("VOLLAR");
//        usdt.add("XRP");
        symbolMap.put("USDT",usdt);
//        List<String> btc = new LinkedList<>();
//        btc.add("AE");
//        btc.add("DASH");
//        btc.add("EOS");
//        btc.add("ETC");
//        btc.add("ETH");
//        btc.add("LTC");
//        btc.add("NEO");
//        btc.add("UPA");
//        btc.add("VOLLAR");
//        btc.add("XRP");
//        symbolMap.put("BTC",btc);
//        List<String> eth = new LinkedList<>();
//        eth.add("AE");
//        eth.add("EOS");
//        eth.add("UPA");
//        symbolMap.put("ETH",eth);
        // spring初始化webSocket
        WebSocketService webSocketService = (WebSocketService) SpringUtil.getBean("ztWebSocketServiceCNT");
        webSocketService.init(symbolMap);
        return symbolMap;

    }
}
