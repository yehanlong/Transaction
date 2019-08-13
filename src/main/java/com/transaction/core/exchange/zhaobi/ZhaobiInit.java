package com.transaction.core.exchange.zhaobi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ZhaobiInit {


    //initSymbol1 对应moving1
    //initSymbol2 对应moving2


    // 初始化需要进行交易的交易对
    public Map<String,List<String>> initSymbol() {
        Map<String,List<String>> symbolMap = new HashMap<>();
        List<String> bty = new LinkedList<>();
        bty.add("YCC");
        symbolMap.put("BTY",bty);
        // btc相关
        List<String> btc = new LinkedList<>();
        btc.add("BTY");
        btc.add("YCC");
        btc.add("BCC");
        btc.add("ETH");
        btc.add("ETC");
        btc.add("ZEC");
        btc.add("LTC");
        symbolMap.put("BTC",btc);
        // eth
        List<String> eth = new LinkedList<>();
        eth.add("BTY");
        eth.add("YCC");
        symbolMap.put("ETH",eth);
        return symbolMap;
    }

}
