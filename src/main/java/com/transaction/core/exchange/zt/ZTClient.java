package com.transaction.core.exchange.zt;

import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pubinterface.Exchange;

import java.util.Map;

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
    public TradeVO getMarketInfo(String symbols) {
        return null;
    }

    @Override
    public SyncMarkInfo getSyncMarkInfo(String symbol1, String symbol2) {
        return null;
    }

    @Override
    public boolean syncPostBill(String symbol1, String symbol2, double amount1, double amount2, double amount3, double price1, double price2, double price3, String type) {
        return false;
    }
}
