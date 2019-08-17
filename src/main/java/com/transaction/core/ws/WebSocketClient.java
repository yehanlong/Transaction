package com.transaction.core.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebSocketClient {
    private WebSocket webSocket;

    private String url;

    private WebSocketService webSocketService;

    private EchoWebSocketListener listener;

    public void init(){
        listener = new EchoWebSocketListener(webSocketService);
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        webSocket = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    public void send(String msg){
        if(getConnected()){
            webSocket.send(msg);
        }
    }

    public boolean getConnected(){
        if(listener == null){
            return false;
        }
        return listener.isConnected();
    }

    public void close(){
        if(webSocket != null){
            webSocket.close(1001,"主动短线重连");
        }
    }
}
