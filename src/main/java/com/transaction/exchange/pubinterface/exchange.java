package com.transaction.exchange.pubinterface;

import com.transaction.entity.account;

import java.util.HashMap;

public interface exchange {

    // 获取账户余额
    // map的key 币种 比如bty
    HashMap<String,account> getAccount(String a);

}
