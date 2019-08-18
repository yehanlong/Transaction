package com.transaction.core.exchange.bidan;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.vo.TradeVO;

public class Testbidan {
    public static void main(String[] args) {
        TradeVO vo = new BiDanClient().getMarketInfo("eth_usdt");
        System.out.println(JSONObject.toJSONString(vo));
    }
}
