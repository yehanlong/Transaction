package com.transaction.core.exchange.pub;

// 挂单类
// 同步挂单和异步挂单，在此次实现，不再去每个交易所实现一次

import com.transaction.core.entity.OrderEntity;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.service.OrderService;
import com.transaction.core.utils.DoubleUtil;
import com.transaction.core.utils.SpringUtil;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class PostBill {

    public static boolean syncPostBill(int accId,int count,Exchange client, String symbol1, String symbol2, String SBase, double amount1,
                                       double amount2, double amount3, double price1, double price12,
                                       double price2, String type){
        CountDownLatch latch = new CountDownLatch(3);
        // todo 增加数据库记录

        if("BUY".equals(type)){
            // 买bty   bty买ycc  卖ycc
            new Thread(()->{
                try {
                    client.postBill(amount1, symbol1,SBase, price1, "BUY");
                    addOrder(accId,count,symbol1+SBase,price1,amount1,"BUY");
                }finally {
                    latch.countDown();
                }
            }).start();

            System.out.println("买入, 交易对: "+ symbol2+symbol1+", 数量: "+ amount2 + ", 价格: "+price12);
            new Thread(()->{
                try {
                    client.postBill(amount2, symbol2,symbol1, price12, type);
                    addOrder(accId,count,symbol2+symbol1,price12,amount2,type);
                }finally {
                    latch.countDown();
                }
            }).start();

            new Thread(()->{
                try {
                    client.postBill(amount3, symbol2,SBase, price2, "SELL");
                    addOrder(accId,count,symbol2+SBase,price12,amount2,"SELL");
                }finally {
                    latch.countDown();
                }
            }).start();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        if("SELL".equals(type)){
            //  买ycc  卖ycc换bty 卖bty

            System.out.println("卖出, 交易对: "+ symbol1+SBase+", 数量: "+ amount1 + ", 价格: "+price1);
            new Thread(()->{
                try {
                    client.postBill(amount1, symbol1,SBase, price1, "SELL");
                    addOrder(accId,count,symbol1+SBase,price1,amount1,"SELL");
                }finally {
                    latch.countDown();
                }
            }).start();

            System.out.println("卖出, 交易对: "+ symbol2+symbol1+", 数量: "+ amount2 + ", 价格: "+price12);
            new Thread(()->{
                try {
                    client.postBill(amount2, symbol2,symbol1, price12, type);
                    addOrder(accId,count,symbol2+symbol1,price12,amount2,type);
                }finally {
                    latch.countDown();
                }
            }).start();

            System.out.println("买入, 交易对: "+ symbol2+SBase+", 数量: "+ amount3 + ", 价格: "+price2);
            new Thread(()->{
                try {
                    client.postBill(amount3, symbol2,SBase, price2, "BUY");
                    addOrder(accId,count,symbol2+SBase,price2,amount3,"BUY");
                }finally {
                    latch.countDown();
                }
            }).start();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }


        return false;
    }

    private static void addOrder(int accId, int count, String s, double price, double amount, String type) {
        OrderService orderService = (OrderService) SpringUtil.getBean("orderService");
        OrderEntity entity = OrderEntity.builder()
                .accountId(accId)
                .count(count)
                .sy(s)
                .price(price)
                .amount(amount)
                .type(type)
                .build();
        orderService.save(entity);

    }


    public static boolean postBill(int accId,int count,Exchange client, String symbol1, String symbol2, String SBase, double amount1,
                                       double amount2, double amount3, double price1, double price12,
                                       double price2, String type){

        // todo 增加数据库记录

        Map<String, PropertyVO> acc = client.getAccount();

        if("BUY".equals(type)){
            // 买bty   bty买ycc  卖ycc


            boolean b = client.postBill(amount1, symbol1,SBase, price1, "BUY");
            if (b) {
                try {
                    Thread.sleep(5100);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

/*
            double btyCount = amount2 * price12 / (1 - client.getSxf());
            if (btyCount >  acc.get(symbol1).getActive()){
                amount2 = acc.get(symbol1).getActive() * (1 - client.getSxf()) / price12;
            }*/

            for (int i = 0; i < 10; i++) {
                if (acc.get(symbol1).getActive() * price1 < PubConst.getAccMin(SBase)){
                    try {
                        System.out.println("获取余额延迟："+ i);
                        Thread.sleep(500);
                        acc = client.getAccount();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            double tmp = DoubleUtil.mul(amount2,price12);
            double btyCount = DoubleUtil.div(tmp,(1-client.getSxf()),15);
            int compareTo1 = DoubleUtil.compareTo(btyCount,acc.get(symbol1).getActive());
            if(compareTo1 == 1){
                double tmp1 = DoubleUtil.mul(acc.get(symbol1).getActive(),(1-client.getSxf()));
                amount2 = DoubleUtil.div(tmp1,price12,15);
            }

            b = client.postBill(amount2, symbol2,symbol1, price12, type);
            if (b) {
                try {
                    Thread.sleep(5200);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < 10; i++) {
                if (acc.get(symbol2).getActive() * price2  < PubConst.getAccMin(SBase)){
                    try {
                        System.out.println("获取余额延迟："+ i);
                        Thread.sleep(501);
                        acc = client.getAccount();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (amount3 > acc.get(symbol2).getActive()){
                amount3 = acc.get(symbol2).getActive();
            }
            b = client.postBill(amount3, symbol2,SBase, price2, "SELL");
            if (b) {
                try {
                    Thread.sleep(5300);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if("SELL".equals(type)){
            //  买ycc  卖ycc换bty 卖bty


            boolean b = client.postBill(amount3, symbol2,SBase, price2, "BUY");
            if (b) {
                try {
                    Thread.sleep(5000);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < 10; i++) {
                if (acc.get(symbol2).getActive() * price2  < PubConst.getAccMin(SBase)){
                    try {
                        System.out.println("获取余额延迟："+ i);
                        Thread.sleep(502);
                        acc = client.getAccount();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (amount2 > acc.get(symbol2).getActive()){
                amount2 = acc.get(symbol2).getActive();
            }
            b = client.postBill(amount2, symbol2,symbol1, price12, type);
            if (b) {
                try {
                    Thread.sleep(6000);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                if (acc.get(symbol1).getActive() * price1  < PubConst.getAccMin(SBase)){
                    try {
                        System.out.println("获取余额延迟："+ i);
                        Thread.sleep(503);
                        acc = client.getAccount();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (amount1 > acc.get(symbol1).getActive()){
                amount1 = acc.get(symbol1).getActive();
            }
            b = client.postBill(amount1, symbol1,SBase, price1, "SELL");
            if (b) {
                try {
                    Thread.sleep(4000);
                    return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }
        return  false;
    }

}
