package com.transaction.core.exchange.pubinterface;

import com.transaction.core.entity.Account;
import com.transaction.core.entity.vo.TradeVO;

import java.util.HashMap;

public interface Exchange {

    // 获取账户余额
    // map的key 币种 比如bty
    HashMap<String,Account> getAccount(String a);

    // 挂单
    boolean postBill(double amount, double currency, double currency2, double price, String ty);

    // 获取市场信息
    TradeVO getMarketInfo(String symbols);

}
