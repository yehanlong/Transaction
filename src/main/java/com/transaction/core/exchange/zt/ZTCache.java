package com.transaction.core.exchange.zt;

import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.TradeVO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zt的本地缓存
 */
public class ZTCache {
    /**
     * ZT交易所订阅挂单信息时，需要保存id值
     */
    public static final ConcurrentHashMap<Integer,String> depthSymbolMap = new ConcurrentHashMap<>();

    /**
     * websocket返回的盘口信息缓存
     */
    public static final ConcurrentHashMap<String, TradeVO> orderMap = new ConcurrentHashMap<>();

    public static volatile String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySWQiOjExNTcxNDQsIkxvZ2luVmVyaWZ5IjoxLCJleHAiOjE1NjY3NDExMDV9.ibrN0ol4Xk19zJ9LjWN3F82q-0ikn_8CnVgFtXn7bcQ";
}
