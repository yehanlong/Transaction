package com.transaction.core.exchange.zt;

import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.TradeVO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * zt的本地缓存
 */
public class ZTCache {
    /**
     * ZT交易所订阅挂单信息时，需要保存id值
     */
    public static final ConcurrentHashMap<Integer,String> depthSymbolMap = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Integer,TradeVO> depthTradeMap = new ConcurrentHashMap<>();

    /**
     * websocket返回的盘口信息缓存
     */
    public static final ConcurrentHashMap<String, TradeVO> orderMap = new ConcurrentHashMap<>();

    public static volatile String token = null;

    public static final ExecutorService service = Executors.newFixedThreadPool(30);
}
