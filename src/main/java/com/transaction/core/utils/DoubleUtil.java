/**
 * Zfounder.com Inc.
 * Copyright (c) 2013-2066 All Rights Reserved.
 */
package com.transaction.core.utils;

import java.math.BigDecimal;

/**
 * @author ja2018
 * @version $Id: DoubleUtil.java, v 0.1 2018/10/12 15:15 ja2018 Exp $
 */


public class DoubleUtil {
    /**
     * double 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sum(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    /**
     * double 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }

    public static double mulThree(double d1, double d2,double d3) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        BigDecimal bd3 = new BigDecimal(Double.toString(d3));
        return bd1.multiply(bd2).multiply(bd3).doubleValue();
    }


    /**
     * double 除法
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double div(double d1, double d2, int scale) {
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 百分比的封装
     *
     * @param molecule    分子
     * @param denominator 分母
     * @param scale       四舍五入 小数点位数
     * @return
     */
    public static double percentageFormatting(double molecule, double denominator, int scale) {
        if (0 == denominator) {
            return 0d;
        }
        double f = molecule / denominator * 100;
        BigDecimal b = new BigDecimal(f);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * double 转 string 去掉后面锝0
     *
     * @param i
     * @return
     */
    public static String getString(double i) {
        String s = String.valueOf(i);
        if (s.indexOf(".") > 0) {
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }


    /**
     * 比较大小
     * 返回值，a>b为1，a<b为-1，a==b为0
     **/
    public static int compareTo(double a,double b){
        BigDecimal a1 = new BigDecimal(a);
        BigDecimal b1 = new BigDecimal(b);
        int c = a1.compareTo(b1);
        return c;
    }


    /**
     * 一些固定公式的封装
     */
    public static double getAnotherSyAmount2Expression(double amount, double price, double sxf){
        //amount*price/(1-0.001)
        BigDecimal amountB = new BigDecimal(amount);
        BigDecimal priceB = new BigDecimal(price);
        BigDecimal express = new BigDecimal(sub(1,sxf));
        double expression = amountB.multiply(priceB).divide(express,25, BigDecimal.ROUND_HALF_UP).doubleValue();
        return  expression;
    }

    public static double getAnotherSyAmount1Expression(double amount, double price, double sxf){
        BigDecimal amountB = new BigDecimal(amount);
        BigDecimal priceB = new BigDecimal(price);
        BigDecimal express = new BigDecimal(sub(1,sxf));
        double expression = amountB.multiply(express).divide(priceB,25, BigDecimal.ROUND_HALF_UP).doubleValue();
        return expression;
    }
}
