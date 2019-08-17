package com.transaction.core.ws;


import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@Slf4j
public class EchoWebSocketListener extends WebSocketListener {

    private WebSocketService socketService;

    private boolean connected = false;

    public EchoWebSocketListener(WebSocketService socketService) {
        super();
        this.socketService = socketService;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log.warn("webSocket open");
        connected = true;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        socketService.onReceive(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        socketService.onReceive(bytes.utf8());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        log.warn("webSocket closing，code:{},reason:{}",code,reason);
        connected = false;
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.warn("webSocket closed");
        connected = false;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        log.error("webSocket failure",t);
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
