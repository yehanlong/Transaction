package com.transaction.core.exchange.pub;

public class PubConst {
    // todo 当不是USDT的时候，需要改变这个值

    // 除了test，别的地方不要调用这个值，应该调用下面的方法
    public static double minUSDT = 1.5;

    public static double getMin(String sBase){
        if (sBase == "USDT"){
            return 1.5;
        }
        if (sBase == "CNT"){
            return 10;
        }
        return 1.5;
    }


    // 获取余额当小于多少时，等待会，重新获取
    public static double getAccMin(String sBase){
        if (sBase == "USDT"){
            return 1;
        }
        if (sBase == "CNT"){
            return 5;
        }
        return 1;
    }
}
