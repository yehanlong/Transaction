package com.transaction.core.exchange;

import com.transaction.core.CoreApplication;
import com.transaction.core.exchange.zhaobi.SyncMoving1;
import com.transaction.core.exchange.zhaobi.SyncMoving2;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;
import com.transaction.core.exchange.zhaobi.ZhaobiInit;
import com.transaction.core.exchange.zt.*;
import com.transaction.core.utils.SpringUtil;
import com.transaction.core.ws.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExStart {

    final static Logger logger = LoggerFactory.getLogger(CoreApplication.class);


    public static void startZhaobi(){
        ZhaobiInit zbi = new ZhaobiInit();
        ZhaobiClient ZBClient = new ZhaobiClient();
        Lock lock = new ReentrantLock();
        //启动线程
        Map<String, List<String>> syMap1 = zbi.initSymbol();
        for (Map.Entry<String, List<String>> entry : syMap1.entrySet()) {
            for (String s: entry.getValue()) {
                SyncMoving1 m1 = new SyncMoving1(ZBClient, entry.getKey(), s);
                m1.setLock(lock);
                m1.start();
            }
        }

        Map<String, List<String>> syMap2 = zbi.initSymbol();
        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
            for (String s: entry.getValue()) {
                SyncMoving2 m2 = new SyncMoving2(ZBClient, entry.getKey(), s);
                m2.setLock(lock);
                m2.start();
            }

        }

    }



    public static void startZT(){
        ZTClient ztClient = new ZTClient();
        ZTInit init = new ZTInit();

        Map<String, List<String>> syMap2 =  init.initSymbol();
        while (true){
            WebSocketClient webSocketClient = (WebSocketClient) SpringUtil.getBean("ztWebSocketClient");
            if(!webSocketClient.getConnected()){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                break;
            }
        }
        logger.info("启动ZT监控...");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
            for (String s: entry.getValue()) {
                MovingBuy m1 = new MovingBuy(ztClient, entry.getKey(), s);
                new Thread(m1,"ZT_"+s+"_"+entry.getKey()).start();
            }

        }

        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
            for (String s: entry.getValue()) {
                MovingSell m2 = new MovingSell(ztClient, entry.getKey(), s);
                new Thread(m2,"ZT_"+s+"_"+entry.getKey()).start();
            }

        }
    }



    public static void startZTCNT(){
        ZTClientCNT ztClient = new ZTClientCNT();
        ZTInit init = new ZTInit();

        Map<String, List<String>> syMap2 =  init.initCNTSymbol();
        while (true){
            WebSocketClient webSocketClient = (WebSocketClient) SpringUtil.getBean("ztWebSocketClient");
            if(!webSocketClient.getConnected()){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                break;
            }
        }
        logger.info("启动ZT监控...");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
            for (String s: entry.getValue()) {
                MovingBuy m1 = new MovingBuy(ztClient, entry.getKey(), s);
                new Thread(m1,"ZTCNT_"+s+"_"+entry.getKey()).start();
            }

        }

        for (Map.Entry<String, List<String>> entry : syMap2.entrySet()) {
            for (String s: entry.getValue()) {
                MovingSell m2 = new MovingSell(ztClient, entry.getKey(), s);
                new Thread(m2,"ZTCNT_"+s+"_"+entry.getKey()).start();
            }

        }
    }
}
