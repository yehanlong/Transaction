package com.transaction.core.exchange.zhaobi;


import java.text.DecimalFormat;

// 处理数量
public class Deal {

    // 处理数量
    public static String dealCount(double count, String sy){
        String result = "error";
        switch (sy){
            case "BTY":
                // 保留小数点后一位
                result = new DecimalFormat("00.0").format(count);
                return result;
            case "YCC":
                // 保留整数位
                result = new DecimalFormat("00.0").format(count);
                return result;
        }
        return result;
    }

    // 处理价格
    public static String dealPrice(double price, String sy){
        String result = "error";
        switch (sy){
            case "BTY":
                // 保留小数点后6位
                result = new DecimalFormat("00.000000").format(price);
                return result;
            case "USDT":
                // 保留小数点后6位
                result = new DecimalFormat("00.000000").format(price);
                return result;
        }
        return result;
    }

}
