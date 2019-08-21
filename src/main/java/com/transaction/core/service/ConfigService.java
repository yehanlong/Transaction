package com.transaction.core.service;

import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.entity.SystemConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigService {
    private static final ConcurrentHashMap<String, SystemConfig> systemConfigs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SymbolConfig> symbolConfigs = new ConcurrentHashMap<>();

    public SymbolConfig getSymbolConfig(String platform,String baseCoin){
        return null;
    }
}
