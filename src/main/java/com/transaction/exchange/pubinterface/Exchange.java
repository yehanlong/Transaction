package com.transaction.exchange.pubinterface;

import com.transaction.entity.Account;

import java.util.HashMap;

public interface Exchange {

    // 获取账户余额
    // map的key 币种 比如bty
    HashMap<String,Account> getAccount(String a);

}
