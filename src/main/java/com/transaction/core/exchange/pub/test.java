package com.transaction.core.exchange.pub;

import com.transaction.core.entity.AmountPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

public class test {

    // 模拟技计算一轮后的usdt是多少  type第二步是买还是卖
    // 测试过，结果正确
    public static BigDecimal getUSDTcount(AmountPrice amountPrice, String type){
        double btyPrice = amountPrice.getSy1Price();
        double btyNum = amountPrice.getSy1Amount();

        double ybPrice = amountPrice.getSy12Price();
        double ybNum = amountPrice.getSy12Amount();

        double yccPrice = amountPrice.getSy2Price();
        double yccNum = amountPrice.getSy2Amount();

        double usdt = 5.0;
        double minUsdt = 1.5;

        if (type == "SELL"){
            // 第一步
            // usdt 买ycc
            BigDecimal yccPriceB = new BigDecimal(yccPrice);
            BigDecimal yccNumB = new BigDecimal(yccNum);
            int a = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(minUsdt));
            if (a == -1) {
                // 当获取失败或者金额太少，就放弃此次循环
                return new BigDecimal(0.0);
            }
            //double yccCount = usdt/yccPrice;
            BigDecimal usdtB = new BigDecimal(usdt);
            BigDecimal yccCountB = usdtB.divide(yccPriceB, 25, RoundingMode.HALF_DOWN);


            // 第二步
            // 卖掉ycc 换bty

            BigDecimal ybPriceB = new BigDecimal(ybPrice);
            BigDecimal ybNumB = new BigDecimal(ybNum);
            int a1 = (ybPriceB.multiply(ybNumB).multiply(new BigDecimal(btyPrice)))
                    .compareTo(new BigDecimal(minUsdt));
            // bty数量*bty价格
            if (a1 == -1) {
                return new BigDecimal(0.0);
            }
            // 最终获得的bty
            //double btyCount = yccCount*ybPrice;
            BigDecimal btyCountB = yccCountB.multiply(ybPriceB);


            // 第三步
            // 卖掉BTY

            BigDecimal btyPriceB = new BigDecimal(btyPrice);
            BigDecimal btyNumB = new BigDecimal(btyNum);
            // 最终获得的usdt
            //double usdtcount = btyPrice*btyCount;
            BigDecimal usdtcountB = btyPriceB.multiply(btyCountB);
            int a2 = (btyPriceB.multiply(btyNumB)).compareTo(new BigDecimal(minUsdt));
            if (a2 == -1) {
                return new BigDecimal(0.0);
            }
            return usdtcountB;
        }

        if (type == "BUY"){
            // usdt 买比特元
            BigDecimal btyPriceB = new BigDecimal(btyPrice);
            BigDecimal btyNumB = new BigDecimal(btyNum);
            int btyB = (btyPriceB.multiply(btyNumB)).compareTo(new BigDecimal(minUsdt));
            if(btyB == -1 ){
                // 当获取失败或者金额太少，就放弃此次循环
                return new BigDecimal(0.0);
            }
            BigDecimal btyCountB = ((new BigDecimal(usdt)).divide(btyPriceB,25,ROUND_HALF_DOWN));
            //double btyCount = usdt/btyPrice;

            // bty 买ycc

            BigDecimal ybPriceB =new BigDecimal(ybPrice);
            BigDecimal ybNumB = new BigDecimal(ybNum);
            int a = (ybPriceB.multiply(ybNumB)).multiply(btyPriceB).compareTo(new BigDecimal(minUsdt));
            if (a == -1) {
                return new BigDecimal(0.0);
            }
            BigDecimal yccCountB = btyCountB.divide(ybPriceB,25,ROUND_HALF_DOWN);
            //double yccCount = btyCount/ybPrice;

            // 卖掉ycc

            BigDecimal yccPriceB =new BigDecimal(yccPrice);
            BigDecimal yccNumB = new BigDecimal(yccNum);
            BigDecimal usdtCountB = yccCountB.multiply(yccPriceB);
            int a1 = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(minUsdt));
            if(a1 == -1){
                return new BigDecimal(0.0);
            }
            return usdtCountB;
        }

        return new BigDecimal(0.0);

    }

}
