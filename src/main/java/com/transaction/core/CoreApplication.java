package com.transaction.core;

import com.transaction.core.entity.SymbolConfig;
import com.transaction.core.entity.SystemConfig;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.service.ConfigService;
import com.transaction.core.strategy.Moving;
import com.transaction.core.strategy.SyncMoving;
import com.transaction.core.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CoreApplication {

    final static Logger logger = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);

        ConfigService configService = (ConfigService) SpringUtil.getBean("configService");
        List<SystemConfig> systemConfigs = configService.getAllEnabledPlatform();
        if(CollectionUtils.isEmpty(systemConfigs)){
            logger.info("未在数据库中配置启用的交易所，程序自动终止");
            System.exit(0);
        }
        for(SystemConfig systemConfig : systemConfigs){
            List<SymbolConfig> symbolConfigs = configService.getAllEnabledSymbol(systemConfig.getPlatform());
            if(CollectionUtils.isEmpty(symbolConfigs)){
                logger.info("交易所{}未配置启用的交易对");
                continue;
            }
            Exchange client = (Exchange) SpringUtil.getBean(systemConfig.getPlatform()+"Client");
            client.init(systemConfig.getPlatform(), symbolConfigs);
            ReentrantLock lock = new ReentrantLock();
            for(SymbolConfig symbolConfig : symbolConfigs){
                // 同步交易策略
                if("moving".equals(symbolConfig.getStrategy())){
                    logger.info("启动{}交易所的{}_{}_{}交易对，交易策略：同步",
                            systemConfig.getPlatform(),symbolConfig.getBaseCoin(),symbolConfig.getSymbol2(),symbolConfig.getSymbol1());
                    Moving moving = new Moving();
                    moving.start(lock,
                            client,
                            symbolConfig.getSymbol1(),
                            symbolConfig.getSymbol2(),
                            symbolConfig.getBaseCoin());
                } else if("syncMoving".equals(symbolConfig.getStrategy())){
                    logger.info("启动{}交易所的{}_{}_{}交易对，交易策略：异步",
                            systemConfig.getPlatform(),symbolConfig.getBaseCoin(),symbolConfig.getSymbol2(),symbolConfig.getSymbol1());
                    SyncMoving moving = new SyncMoving();
                    moving.start(lock,
                            client,
                            symbolConfig.getSymbol1(),
                            symbolConfig.getSymbol2(),
                            symbolConfig.getBaseCoin());
                }
            }
        }
    }




}
