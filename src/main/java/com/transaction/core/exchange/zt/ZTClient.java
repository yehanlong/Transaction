package com.transaction.core.exchange.zt;

import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZTClient implements Exchange{
    @Override
    public Map<String, PropertyVO> getAccount() {
        return null;
    }

    @Override
    public boolean postBill(double amount, String currency, String currency2, double price, String ty) {
        return false;
    }

    @Override
    public TradeVO getMarketInfo(String symbol) {
        TradeVO vo = ZTCache.orderMap.get(symbol);
        return vo;
    }

    @Override
    public SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2) {
        SyncMarkInfo info = new SyncMarkInfo();
        String s1USDT = symbol1 + "_" + "USDT";
        String s2s1 = symbol2 + "_" + "USDT";
        String s2USDT = symbol2 + "_" + symbol1;
        while (true){
            TradeVO s1Trade = ZTCache.orderMap.get(s1USDT);
            TradeVO s2Trade = ZTCache.orderMap.get(s2USDT);
            TradeVO s2s1Trade = ZTCache.orderMap.get(s2s1);
            long currentTime = System.currentTimeMillis();
            if(currentTime-s1Trade.getTime() > 1000
                    || currentTime - s2Trade.getTime() > 1000
                    || currentTime-s2s1Trade.getTime() > 1000){
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            info.setTrade1(s1Trade);
            info.setTrade2(s2Trade);
            info.setTrade3(s2s1Trade);
            return info;
        }

    }

    @Override
    public boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2, double amount3, double price1, double price2, double price3, String type) {
        return false;
    }
}
