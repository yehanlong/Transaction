package com.transaction.core.exchange.pubinterface;

import com.transaction.core.entity.Account;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.entity.vo.PropertyVO;

import java.util.HashMap;
import java.util.Map;

public interface Exchange {

    // 获取账户余额
    // map的key 币种 比如bty
    PropertyVO getAccount(String a);

    // 挂单
    // 如postBill(1,"YCC","USDT",0.013161,"SELL");数量为1的ycc卖成usdt
    boolean postBill(double amount, String currency, String currency2, double price, String ty);

    // 获取市场信息
    // 如getMarketInfo("YCCUSDT");
    TradeVO getMarketInfo(String symbols);

    // 异步获取市场行情 symbol1: BTY   symbol2:YCC
    // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
    SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2);

    // 异步挂单
    // type指第二步买还是卖
    boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2,double amount3,
                         double price1, double price2,double price3, String type);

}
