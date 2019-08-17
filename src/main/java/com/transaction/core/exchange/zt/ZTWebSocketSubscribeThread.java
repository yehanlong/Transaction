package com.transaction.core.exchange.zt;

import com.transaction.core.ws.WebSocketClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@Slf4j
public class ZTWebSocketSubscribeThread implements Runnable {
    private Set<String> symbols;
    private String subject;
    private WebSocketClient webSocketClient;
    @Override
    public void run() {
        while (true){
            for(String symbol : symbols){
                try {
                    // 以hashCode值作为id
                    int id = ("depth.query"+symbol).hashCode();
                    String str = String.format(subject,symbol,id);
                    webSocketClient.send(str);
                    ZTCache.depthSymbolMap.put(id,symbol);
                }catch (Exception e){
                    log.error("订阅"+symbol+"主题"+subject+"失败",e);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
