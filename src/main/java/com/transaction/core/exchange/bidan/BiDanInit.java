package com.transaction.core.exchange.bidan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BiDanInit {

    // 初始化需要进行交易的交易对
    public Map<String,List<String>> initSymbol() {
        Map<String,List<String>> symbolMap = new HashMap<>();
//        List<String> bty = new LinkedList<>();
//        bty.add("YCC");
//        symbolMap.put("BTY",bty);
        // btc相关
        List<String> btc = new LinkedList<>();
        btc.add("ETH");
        btc.add("LTC");
        btc.add("EOS");
        btc.add("XRP");
        btc.add("ETC");
        btc.add("GRIN");
        btc.add("TRX");
        btc.add("NEO");
        btc.add("ONT");
        btc.add("QTUM");
        btc.add("DOGE");
        btc.add("BTT");
        btc.add("AOA");
        btc.add("GAC");
        btc.add("BIUT");
        btc.add("TGC");
        symbolMap.put("BTC",btc);
        // eth
        List<String> eth = new LinkedList<>();
        eth.add("LTC");
        eth.add("XRP");
        eth.add("NEO");
        eth.add("QTUM");
        eth.add("ONT");
        eth.add("BIUT");
        symbolMap.put("ETH",eth);


        return symbolMap;
    }
}
