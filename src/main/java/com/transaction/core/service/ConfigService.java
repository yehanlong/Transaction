package com.transaction.core.service;

import com.transaction.core.dao.SymbolConfigDao;
import com.transaction.core.dao.SystemConfigDao;
import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.entity.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigService {
    private static final ConcurrentHashMap<String, SystemConfig> systemConfigs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SymbolConfig> symbolConfigs = new ConcurrentHashMap<>();
    // 根据平台名+交易品种+交易对名称为缓存的key，方便根据交易对找到配置，如ZT_USDT_CNT,ZT_LTC_USDT,ZT_ETH_BTC,ZT_EOS_ETH
    private static final ConcurrentHashMap<String, Integer> smallCountConfigs = new ConcurrentHashMap<>();

    @Value("${cacheTime}")
    private Integer cacheTime;

    @Autowired
    private SymbolConfigDao symbolConfigDao;

    @Autowired
    private SystemConfigDao systemConfigDao;

    private final Object symbol = new Object();
    private final Object system = new Object();

    /**
     * 根据平台和交易对查询缓存
     * @param platform
     * @param baseCoin
     * @param symbol1
     * @param symbol2
     * @return
     */
    public SymbolConfig getSymbolConfig(String platform,String baseCoin,String symbol1,String symbol2){
        String key = getKey(platform,baseCoin,symbol1,symbol2);
        SymbolConfig symbolConfig = symbolConfigs.get(key);
        // 双重校验锁更新缓存
        if(symbolConfig == null || System.currentTimeMillis() - symbolConfig.getCacheTime().getTime() > cacheTime){
            synchronized (symbol){
                symbolConfig = symbolConfigs.get(key);
                if(symbolConfig == null || System.currentTimeMillis() - symbolConfig.getCacheTime().getTime() < cacheTime){
                    symbolConfig = symbolConfigDao.getBySymbol(platform,baseCoin,symbol1,symbol2);
                    if(symbolConfig == null){
                        throw new RuntimeException("数据库交易对配置"+key+"不存在");
                    }
                    symbolConfig.setCacheTime(new Date());
                    symbolConfigs.put(key,symbolConfig);
                    smallCountConfigs.put(symbolConfig.getPlatform()+"_"+symbolConfig.getSymbol1()+"_"+symbolConfig.getBaseCoin(),
                            symbolConfig.getBaseSymbol1Amount());
                    smallCountConfigs.put(symbolConfig.getPlatform()+"_"+symbolConfig.getSymbol2()+"_"+symbolConfig.getBaseCoin(),
                            symbolConfig.getBaseSymbol2Amount());
                    smallCountConfigs.put(symbolConfig.getPlatform()+"_"+symbolConfig.getSymbol2()+"_"+symbolConfig.getSymbol1(),
                            symbolConfig.getBaseSymbol1Amount());
                }
            }
        }
        return symbolConfig;
    }

    /**
     * 根据平台名称获取平台配置
     * @param platform
     * @return
     */
    public SystemConfig getSystemConfig(String platform){
        SystemConfig systemConfig = systemConfigs.get(platform);
        // 双重校验锁更新缓存
        if(systemConfig == null || System.currentTimeMillis() - systemConfig.getCacheTime().getTime() > cacheTime){
            synchronized (system){
                systemConfig = systemConfigs.get(platform);
                if(systemConfig == null || System.currentTimeMillis() - systemConfig.getCacheTime().getTime() < cacheTime){
                    systemConfig = systemConfigDao.getByPlatform(platform);
                    if(systemConfig == null){
                        throw new RuntimeException("数据库交易对平台配置"+platform+"不存在");
                    }
                    systemConfig.setCacheTime(new Date());
                    systemConfigs.put(platform,systemConfig);
                }
            }
        }
        return systemConfig;
    }

    /**
     * 获取交易对的最小交易数量限制
     * @param platform 平台名称
     * @param sy1 主交易对名称，如BTC/USDT的主交易对是USDT，ETH/BTC的主交易对是BTC
     * @param sy2 交易品种的名称
     * @return
     */
    public Integer getSmallAmount(String platform, String sy1,String sy2){
        return smallCountConfigs.get(platform + "_" + sy2 +"_" + sy1);
    }

    /**
     * 获取平台所有启用的交易对
     * @param platform
     * @return
     */
    public List<SymbolConfig> getAllEnabledSymbol(String platform){
        return symbolConfigDao.getByPlatformAndEnabled(platform,true);
    }

    /**
     * 获取所有启用的平台
     * @return
     */
    public List<SystemConfig> getAllEnabledPlatform(){
        return systemConfigDao.getByEnabled(true);
    }

    private String getKey(String platform,String baseCoin,String symbol1,String symbol2){
        return platform+"_"+baseCoin+"_"+symbol1+"_"+symbol2;
    }

}
