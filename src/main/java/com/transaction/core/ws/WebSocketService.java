package com.transaction.core.ws;


import java.util.List;
import java.util.Map;

public interface WebSocketService {
    void onReceive(String message);

    void onReset();

    void init(Map<String,List<String>> symbolMap);
}
