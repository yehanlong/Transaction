package com.transaction.core;

import com.transaction.core.exchange.zhaobi.Moving1;
import com.transaction.core.exchange.zhaobi.Moving2;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;
import com.transaction.core.exchange.zhaobi.ZhaobiInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CoreApplication {

    final static Logger logger = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
        logger.error("测试log");

        ZhaobiInit zbi = new ZhaobiInit();
        ZhaobiClient ZBClient = new ZhaobiClient();
        Lock lock = new ReentrantLock();
        //启动线程
        Map<String, String> syMap1 = zbi.initSymbol1();
        for (Map.Entry<String, String> entry : syMap1.entrySet()) {
            Moving1 m1 = new Moving1(ZBClient, entry.getKey(), entry.getValue());
            m1.setLock(lock);
            m1.start();
        }

        Map<String, String> syMap2 = zbi.initSymbol2();
        for (Map.Entry<String, String> entry : syMap2.entrySet()) {
            Moving2 m2 = new Moving2(ZBClient, entry.getKey(), entry.getValue());
            m2.setLock(lock);
            m2.start();
        }

//;
    }

}
