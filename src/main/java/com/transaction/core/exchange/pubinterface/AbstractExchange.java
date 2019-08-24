package com.transaction.core.exchange.pubinterface;

import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.service.ConfigService;
import com.transaction.core.utils.SpringUtil;

import java.math.BigDecimal;

public abstract class AbstractExchange implements Exchange {
    @Override
    public int getSleepTime() {
        return getConfig().getSystemConfig(getName()).getLoopSleepTime();
    }

    @Override
    public double showlogPrice() {
        return getConfig().getSystemConfig(getName()).getLogOutLimit().doubleValue();
    }

    @Override
    public double getStartPercentage() {
        return getConfig().getSystemConfig(getName()).getStartPercentage().doubleValue();
    }

    @Override
    public double getEveryUSDT(String sy1, String sy2, String sBase) {
        return getConfig().getSymbolConfig(getName(),sBase,sy1,sy2).getBaseCoinEveryAmount().doubleValue();
    }

    @Override
    public String getSmallCount(String sy1, String sy2) {
        return BigDecimal.ZERO.setScale(getConfig().getSmallAmount(getName(),sy1,sy2),BigDecimal.ROUND_DOWN).toString();
    }

    private ConfigService getConfig(){
        return (ConfigService) SpringUtil.getBean("configService");
    }
}
