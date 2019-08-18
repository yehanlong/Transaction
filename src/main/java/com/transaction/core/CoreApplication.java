package com.transaction.core;

import com.transaction.core.exchange.ExStart;
import com.transaction.core.exchange.ExStartConst;
import com.transaction.core.exchange.zhaobi.*;
import com.transaction.core.exchange.zt.MovingBuy;
import com.transaction.core.exchange.zt.MovingSell;
import com.transaction.core.exchange.zt.ZTClient;
import com.transaction.core.exchange.zt.ZTInit;
import com.transaction.core.utils.SpringUtil;
import com.transaction.core.ws.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CoreApplication {

    final static Logger logger = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);

        // 启动找币
        if (ExStartConst.ZHAOBISTART == 1){
            ExStart.startZhaobi();
        }

        // ZT
        if (ExStartConst.ZTSTART == 1){
            ExStart.startZT();
        }

        // ZT  CNT
        if (ExStartConst.ZTCNTSTART == 1) {
            ExStart.startZTCNT();
        }

    }




}
