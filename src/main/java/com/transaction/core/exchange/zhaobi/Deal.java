package com.transaction.core.exchange.zhaobi;


import com.transaction.core.entity.AmountPrice;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.utils.DoubleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

// 处理数量
public class Deal {

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    // 处理数量
    public static String dealCount(double count, String sy){
        String result = "error";
        switch (sy){
            case "BTY":
                // 保留小数点后一位
                result = new DecimalFormat("0.0").format(count);
                return result;
            case "YCC":
                // 保留整数位
                result = new DecimalFormat("0").format(count);
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
                result = new DecimalFormat("0.000000").format(price);
                return result;
            case "USDT":
                // 保留小数点后6位
                result = new DecimalFormat("0.000000").format(price);
                return result;
        }
        return result;
    }

    // p1 p2 p3 分别是第一二三步具体的usdt  type第二步是买还是卖
    // sy1永远都是bty 代表usdt
    public static AmountPrice getAcuallyUSDT(AmountPrice amountPrice, String type){


        double sy1;
        double sy12;
        double sy2;

        if (type=="BUY"){
            // 买bty ，bty 买 ycc， 。卖掉ycc
            sy1 = amountPrice.getSy1Amount()*amountPrice.getSy1Price();
            // ycc数量*ycc价格
            sy12 = amountPrice.getSy12Amount()*amountPrice.getSy12Price()*amountPrice.getSy1Price();
            sy2 = amountPrice.getSy2Amount()*amountPrice.getSy2Price();
        }else {
            // 买ycc ，卖ycc 得到bty。卖掉bty
            sy1 = amountPrice.getSy1Amount()*amountPrice.getSy1Price();
            // 换成bty 再乘bty价格
            sy12 = amountPrice.getSy12Amount()*amountPrice.getSy12Price()*amountPrice.getSy1Price();
            sy2 = amountPrice.getSy2Amount()*amountPrice.getSy2Price();
        }


        // 获取最小的
        double min = sy1;
        if(sy12 < min) {
            min = sy12;
        }
        if (sy2 < min) {
            min = sy2;
        }

        AmountPrice a = new AmountPrice();
        a.setSy1Price(amountPrice.getSy1Price());
        a.setSy2Price(amountPrice.getSy2Price());
        a.setSy12Price(amountPrice.getSy12Price());
        a.setMinUSDT(min);

        // 买bty ，bty 买 ycc， 。卖掉ycc
        if(type == "BUY") {
            if (min == sy1){
                // 以第一步为基准 可以知道多少bty
                double bty = amountPrice.getSy1Amount();
                a.setSy1Amount(bty);
                // 第二步的数量 根据第一步的bty来获取
                double ycc = getAnotherSyAmount1(bty,amountPrice.getSy12Price(),type);
                // 第二步买入多少ycc  第三步就卖出多少ycc
                a.setSy12Amount(ycc);
                a.setSy2Amount(ycc);

            }

            if (min == sy12){
                // 以第二步为基准  能知道第二步买入多少ycc
                double ycc = amountPrice.getSy12Amount();
                // 获取第一步所需要的bty数量
                double bty = getAnotherSyAmount2(ycc,amountPrice.getSy12Price(),type);
                a.setSy1Amount(bty);
                a.setSy12Amount(ycc);
                a.setSy2Amount(ycc);

            }

            if (min == sy2){
                // 以第三步为基准 卖出多少ycc
                double ycc = amountPrice.getSy2Amount();
                // 第一步根据ycc 获取bty
                double bty = getAnotherSyAmount2(ycc,amountPrice.getSy12Price(),type);

                a.setSy1Amount(bty);
                // 第二步买入多少ycc
                a.setSy12Amount(ycc);
                a.setSy2Amount(ycc);

            }
        }

        // 买ycc ，卖ycc 得到bty。卖掉bty
        if(type == "SELL") {
            if (min == sy1){
                a.setMinUSDT(sy1);
                // 以第三步为基准 可以知道卖出多少bty
                double bty = amountPrice.getSy1Amount();

                // 第二步的数量 根据第3步的bty来获取  // 需要知道卖掉多少ycc才能买到第三步的bty
                double ycc = getAnotherSyAmount1(bty,amountPrice.getSy12Price(),type);

                // 第2步卖出多少ycc  第1步就卖出多少ycc

                //第一步买入多少ycc
                a.setSy2Amount(ycc);
                //第二步 卖出多少ycc 获得bty
                a.setSy12Amount(ycc);
                //第三步 卖出多少bty
                a.setSy1Amount(bty);

            }

            if (min == sy12){
                a.setMinUSDT(sy12);
                // 以第二步为基准  能知道第二步卖出多少ycc
                double ycc = amountPrice.getSy12Amount();
                // 获取第2步所得到的bty数量
                double bty = getAnotherSyAmount2(ycc,amountPrice.getSy12Price(),type);

                //第一步买入多少ycc
                a.setSy2Amount(ycc);
                // 第二步卖出多少ycc
                a.setSy12Amount(ycc);
                //第三步卖出多少bty
                a.setSy1Amount(bty);

            }

            if (min == sy2){
                // 以第1步为基准 买入多少ycc
                double ycc = amountPrice.getSy2Amount();
                // ycc 第二步卖出ycc能获取多少bty获取bty
                double bty = getAnotherSyAmount2(ycc,amountPrice.getSy12Price(),type);

                //第一步买入多少ycc
                a.setSy2Amount(ycc);
                // 第二步卖出多少ycc
                a.setSy12Amount(ycc);
                //第三步卖出多少bty
                a.setSy1Amount(bty);

            }
        }



        return a;
    }


    // 都是根据bty获取ycc数量
    //  第一步买入比特元，有bty的数量，需要知道能在第二步买多少ycc
    // amount bty  price bty-ycc    bty*(1-0.001)/price = ycc

    //  第3步卖出比特元，有bty的数量，需要知道第二步卖多少ycc能得到bty ycc为手续费
    // amount bty  price bty-ycc    ycc*(1-0.001)*price = bty   ycc = bty/(1-0.001)/price
    public static double getAnotherSyAmount1(double amount, double price, String type){
        if (type == "BUY"){
            return  DoubleUtil.getAnotherSyAmount1Expression(amount, price);
        }
        if (type == "SELL"){
            return  DoubleUtil.getAnotherSyAmount1Expression(amount, price);
        }

        return 0;
    }


    // 都是根据ycc获取bty数量
    // 0.052  24.34  23.18  0.0157  买100个就是100个  手续费从usdt扣
    // bty 买 ycc  amount=ycc  price = bty-ycc  返回需要第一步购买的bty数量  bty*(1-0.001) = ycc * price  bty = ( ycc * price)/(1-0.001)
    //  第三步只需要卖掉第二步ycc数量就行  bty做手续费 所有bty稍微多点
    // ycc 卖掉获取bty  amount = ycc 需要知道我要在第三步卖掉多少bty  第一步只要买同样ycc就行 ycc*(1-0.001) = bty/price    bty =  ycc*(1-0.001)*price
    // bty做手续费 所有bty稍微少点
    public static double getAnotherSyAmount2(double amount, double price, String type){
        if (type == "BUY"){
            //return amount*price/(1-0.001);
            return DoubleUtil.getAnotherSyAmount2Expression(amount,price);
        }

        if (type == "SELL"){
            //return amount*price*(1-0.001);
            return DoubleUtil.getAnotherSyAmount2Expression(amount, price);
        }

        return 0;
    }




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

    // 第一个参数是预计usdt，第二个是可吃的usdt，第三个参数是最大usdt限制
    public static double getEveryUsdt(double usdt1,double usdt2, double max){

        Random r = new Random(1);

        if (max == 2.0) {
            return 1.5+r.nextDouble()*3;
        }

        // 这个放前面，优先级最高
        if (usdt1 > 5.1) {
            return 8 + r.nextDouble()*3;
        }

        if (usdt2 > 100){
            return 5+r.nextDouble()*6;
        }

        return 4+r.nextDouble()*3;
    }


    public static Order dealSmallOrder(List<Order> o) {
        double usdt = DoubleUtil.mul(o.get(0).getPrice(),o.get(0).getAm());
        if(DoubleUtil.compareTo(usdt,1.5) == -1){
            usdt = DoubleUtil.mul(o.get(0).getPrice(),o.get(0).getAm());
            if(DoubleUtil.compareTo(usdt,1.5) == -1){
                return new Order(o.get(2).getPrice(),o.get(2).getAm());
            }else {
                return new Order(o.get(1).getPrice(),o.get(1).getAm());
            }
        }else {
            return new Order(o.get(0).getPrice(),o.get(0).getAm());
        }
    }


    public static void main(String[] args){

        Random r = new Random();
        System.out.println(7+r.nextDouble()*3);
        AmountPrice a = new AmountPrice();

        //time="2019-08-11T05:19:20+08:00" level=warning msg="usdtCount: 5.061817"
        //time="2019-08-11T05:19:20+08:00" level=warning msg="amout: 338.385977, currency： YCC, currency2:  USDT, price: 0.011955, ty: SELL"
        //价格:  0.011955
        //数量： 338
        //time="2019-08-11T05:19:20+08:00" level=warning msg="amout: 16.597510, currency： BTY, currency2:  USDT, price: 0.241000, ty: BUY"
        //价格:  0.2410
        //数量： 16.9
        //time="2019-08-11T05:19:20+08:00" level=warning msg="amout: 338.385977, currency： YCC, currency2:  BTY, price: 0.049000, ty: BUY"
        //价格:  0.049000
        //数量： 338
        // bty买ycc
        a.setSy1Price(0.2410);
        a.setSy1Amount(1000);
        a.setSy12Price(0.0490000);
        a.setSy12Amount(1000);
        a.setSy2Price(0.011955);
        a.setSy2Amount(1000);
//        System.out.println(getUSDTcount1(a,"BUY"));


        // 卖ycc换bty  正确
        a.setSy1Price(0.2431);
        a.setSy1Amount(1000);
        a.setSy12Price(0.049800);
        a.setSy12Amount(1000);
        a.setSy2Price(0.012309);
        a.setSy2Amount(1000);
//        System.out.println(getUSDTcount1(a,"SELL"));

        // 测试getAcuallyUSDT
        a.setSy1Price(0.2431);
        a.setSy1Amount(1000);
        a.setSy12Price(0.049800);
        a.setSy12Amount(500);
        a.setSy2Price(0.012309);
        a.setSy2Amount(1000);
        System.out.println(getAcuallyUSDT(a,"SELL"));
        System.out.println(getAcuallyUSDT(a,"BUY"));
        a.setSy1Price(0.2431);
        a.setSy1Amount(5);
        a.setSy12Price(0.049800);
        a.setSy12Amount(1000);
        a.setSy2Price(0.019309);
        a.setSy2Amount(1000);
        System.out.println(getAcuallyUSDT(a,"SELL"));
        System.out.println(getAcuallyUSDT(a,"BUY"));

        a.setSy1Price(0.2431);
        a.setSy1Amount(1000);
        a.setSy12Price(0.049800);
        a.setSy12Amount(1000);
        a.setSy2Price(0.012309);
        a.setSy2Amount(200);
        System.out.println(getAcuallyUSDT(a,"SELL"));
        System.out.println(getAcuallyUSDT(a,"BUY"));

    }


}
