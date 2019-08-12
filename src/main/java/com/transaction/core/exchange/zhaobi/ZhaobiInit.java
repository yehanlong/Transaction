package com.transaction.core.exchange.zhaobi;

import java.util.HashMap;
import java.util.Map;

public class ZhaobiInit {


    //initSymbol1 对应moving1
    //initSymbol2 对应moving2


    // 初始化需要进行交易的交易对
    public Map<String,String> initSymbol1() {
        Map<String,String> symbolMap = new HashMap<>();
        symbolMap.put("BTY","YCC");
        // btc相关
        symbolMap.put("BTC","BTY");
        symbolMap.put("BTC","YCC");
        symbolMap.put("BTC","BCH");
        symbolMap.put("BTC","ETH");
        symbolMap.put("BTC","ETC");
        symbolMap.put("BTC","ZEC");
        symbolMap.put("BTC","LTC");
        // eth
        symbolMap.put("ETH","BTY");
        symbolMap.put("ETH","YCC");
        return symbolMap;
    }

    public Map<String,String> initSymbol2() {
        Map<String,String> symbolMap = new HashMap<>();
        symbolMap.put("BTY","YCC");
        // btc相关
        symbolMap.put("BTC","BTY");
        symbolMap.put("BTC","YCC");
        symbolMap.put("BTC","BCH");
        symbolMap.put("BTC","ETH");
        symbolMap.put("BTC","ETC");
        symbolMap.put("BTC","ZEC");
        symbolMap.put("BTC","LTC");
        // eth
        symbolMap.put("ETH","BTY");
        symbolMap.put("ETH","YCC");
        return symbolMap;
    }
}
