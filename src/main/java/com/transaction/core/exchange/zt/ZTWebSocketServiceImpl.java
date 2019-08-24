package com.transaction.core.exchange.zt;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.exchange.zt.handle.ZTHandleMessage;
import com.transaction.core.ws.WebSocketClient;
import com.transaction.core.ws.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("ztWebSocketService")
public class ZTWebSocketServiceImpl implements WebSocketService {

    private final String url = "wss://ws.zt.com/ws";

    private volatile static Set<String> symbolSet = Collections.synchronizedSet(new HashSet<>());

    @Resource(name = "ztWebSocketClient")
    @Lazy
    private WebSocketClient webSocketClient;

    @Autowired
    private List<ZTHandleMessage> handlers;

    @Override
    public void onReceive(String message) {
        boolean handle = false;
        JSONObject object = JSONObject.parseObject(message);

//        log.info("symbol:{},message:{}",ZTCache.depthSymbolMap.get(object.getInteger("id")),message);
        for(ZTHandleMessage handleMessage : handlers){
            if (handleMessage.handleType(object)){
                handleMessage.handle(object);
                handle = true;
            }
        }
        Integer id = object.getInteger("id");
        if(!handle){
//            log.info("{}->{}",ZTCache.depthSymbolMap.get(id),message);
        }
    }

    @Override
    public void onReset() {

    }

    @Override
    public void init(List<SymbolConfig> symbolConfigs) {
        webSocketClient.setUrl(url);
        webSocketClient.init();
        for(SymbolConfig symbolConfig : symbolConfigs){
            symbolSet.add(symbolConfig.getSymbol2() + "_" + symbolConfig.getSymbol1());
            symbolSet.add(symbolConfig.getSymbol1() + "_" + symbolConfig.getBaseCoin());
            symbolSet.add(symbolConfig.getSymbol2() + "_" + symbolConfig.getBaseCoin());
        }
        // 启动主题订阅线程
        String subject = "{\"method\":\"depth.query\",\"params\":[\"%s\",10,\"0.00000001\"],\"id\":%d}";
        ZTWebSocketSubscribeThread thread = new ZTWebSocketSubscribeThread(symbolSet,subject,webSocketClient);
        new Thread(thread,"ZtMonitor").start();

        // 启动ping远程服务器线程
        new Thread(new PingThread(),"pingThread").start();

        // 启动websocket断开连接自动重连线程
        new Thread(new ReConnectThread(), "ReConnectThread").start();
    }

    private class ReConnectThread implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!webSocketClient.getConnected()){
                    try {
                        webSocketClient.init();
                    }catch (Exception e){
                        log.info("websocket重连失败");
                    }
                }

            }
        }
    }

    private class PingThread implements Runnable{

        @Override
        public void run() {
            String ping = "{\"method\":\"server.ping\",\"params\":[],\"id\":%d}";
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(webSocketClient.getConnected()){
                    try {
                        webSocketClient.send(String.format(ping,System.currentTimeMillis()));
                    }catch (Exception e){
                        log.error("发送Ping信息失败",e);
                    }
                }

            }
        }
    }
}
