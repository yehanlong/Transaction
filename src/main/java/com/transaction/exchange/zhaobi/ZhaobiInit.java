package com.transaction.exchange.zhaobi;

import java.util.HashMap;
import java.util.Map;

public class ZhaobiInit {


    //initSymbol1 对应moving1
    //initSymbol2 对应moving2


    // 初始化需要进行交易的交易对
    public Map<String,String> initSymbol1() {
        Map<String,String> symbolMap = new HashMap<>();
        symbolMap.put("BTY","YCC");
        symbolMap.put("1","1");
        symbolMap.put("2","2");
        return symbolMap;
    }

    public Map<String,String> initSymbol2() {
        Map<String,String> symbolMap = new HashMap<>();
        symbolMap.put("YCC","BTY");
        return symbolMap;
    }
}
