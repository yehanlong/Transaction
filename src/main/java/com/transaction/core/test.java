package com.transaction.core;

import com.transaction.core.exchange.zhaobi.Moving1;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;

public class test {

    public static void main(String[] args) {
        ZhaobiClient ZBClient = new ZhaobiClient();
        Moving1 m1 = new Moving1(ZBClient, "BTY", "YCC");
        m1.run();
    }
}
