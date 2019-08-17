package com.transaction.core;

import com.transaction.core.exchange.zhaobi.*;
import com.transaction.core.exchange.zt.ZTInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CoreApplication {

    final static Logger logger = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);

//        ZhaobiInit zbi = new ZhaobiInit();
//        ZhaobiClient ZBClient = new ZhaobiClient();
//        Lock lock = new ReentrantLock();
//        //启动线程
//        Map<String, List<String>> syMap1 = zbi.initSymbol();
//        for (Map.Entry<String, List<String>> entry : syMap1.entrySet()) {
//            for (String s: entry.getValue()) {
//                SyncMoving1 m1 = new SyncMoving1(ZBClient, entry.getKey(), s);
//                m1.setLock(lock);
//                m1.start();
//            }
//        }
//
//        Map<String, List<String>> syMap2 = zbi.initSymbol();
//        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
//            for (String s: entry.getValue()) {
//                SyncMoving2 m2 = new SyncMoving2(ZBClient, entry.getKey(), s);
//                m2.setLock(lock);
//                m2.start();
//            }
//
//        }
        ZTInit init = new ZTInit();
        init.initSymbol();

//;
    }

}
