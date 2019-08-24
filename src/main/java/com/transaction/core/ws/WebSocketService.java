package com.transaction.core.ws;


import com.transaction.core.entity.SymbolConfig;

import java.util.List;
import java.util.Map;

public interface WebSocketService {
    void onReceive(String message);

    void onReset();

    void init(List<SymbolConfig> symbolConfigs);
}
