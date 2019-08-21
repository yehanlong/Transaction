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
    Map<String, PropertyVO> getAccount();

    // 挂单
    // 如postBill(1,"YCC","USDT",0.013161,"SELL");数量为1的ycc卖成usdt
    boolean postBill(double amount, String currency, String currency2, double price, String ty);

    // 获取市场信息
    // 如getMarketInfo("YCCUSDT");
    TradeVO getMarketInfo(String sy1, String sy2);

    // 异步获取市场行情 symbol1: BTY   symbol2:YCC
    // SyncMarkInfo: trade1 bty trade2 bty-ycc trade3 ycc
    SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2, String SBase);

    // 异步挂单
    // type指第二步买还是卖
    boolean syncPostBill(String symbol1, String symbol2, String SBase, double amount1, double amount2,double amount3,
                         double price1, double price2,double price3, String type);

    // 交易所名字
    String getName();


    // 每次运行的睡眠时间
    int getSleepTime();

    // 当大于多少时，显示预计usdt的日志
    double showlogPrice();

    // 开始交易的百分比
    double getStartPrecentage();

    // 获取每次交易的usdt
    double getEveryUSDT();

}
