package com.transaction.core.exchange.pub;

// 挂单类
// 同步挂单和异步挂单，在此次实现，不再去每个交易所实现一次

import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.exchange.pubinterface.Exchange;
import com.transaction.core.utils.DoubleUtil;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class PostBill {

    public static boolean syncPostBill(Exchange client, String symbol1, String symbol2, String SBase, double amount1,
                                       double amount2, double amount3, double price1, double price12,
                                       double price2, String type){
        CountDownLatch latch = new CountDownLatch(3);

        if("BUY".equals(type)){
            // 买bty   bty买ycc  卖ycc
            new Thread(()->{
                try {
                    client.postBill(amount1, symbol1,SBase, price1, "BUY");
                }finally {
                    latch.countDown();
                }
            }).start();

            new Thread(()->{
                try {
                    client.postBill(amount2, symbol2,symbol1, price12, type);
                }finally {
                    latch.countDown();
                }
            }).start();

            new Thread(()->{
                try {
                    client.postBill(amount3, symbol2,SBase, price2, "SELL");
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

            new Thread(()->{
                try {
                    client.postBill(amount1, symbol1,SBase, price1, "SELL");
                }finally {
                    latch.countDown();
                }
            }).start();

            new Thread(()->{
                try {
                    client.postBill(amount2, symbol2,symbol1, price12, type);
                }finally {
                    latch.countDown();
                }
            }).start();

            new Thread(()->{
                try {
                    client.postBill(amount3, symbol2,SBase, price2, "BUY");
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

    public static boolean postBill(Exchange client, String symbol1, String symbol2, String SBase, double amount1,
                                       double amount2, double amount3, double price1, double price12,
                                       double price2, String type){

        Map<String, PropertyVO> acc = client.getAccount();

        if("BUY".equals(type)){
            // 买bty   bty买ycc  卖ycc


            client.postBill(amount1, symbol1,SBase, price1, "BUY");


/*            // todo double 处理
            double btyCount = amount2 * price12 / (1 - client.getSxf());
            if (btyCount >  acc.get(symbol1).getActive()){
                amount2 = acc.get(symbol1).getActive() * (1 - client.getSxf()) / price12;
            }*/

            double tmp = DoubleUtil.mul(amount2,price12);
            double btyCount = DoubleUtil.div(tmp,(1-client.getSxf()),15);
            int compareTo1 = DoubleUtil.compareTo(btyCount,acc.get(symbol1).getActive());
            if(compareTo1 == 1){
                double tmp1 = DoubleUtil.mul(acc.get(symbol1).getActive(),(1-client.getSxf()));
                amount2 = DoubleUtil.div(tmp1,price12,15);
            }

            client.postBill(amount2, symbol2,symbol1, price12, type);


            if (amount3 > acc.get(symbol2).getActive()){
                amount3 = acc.get(symbol2).getActive();
            }
            client.postBill(amount3, symbol2,SBase, price2, "SELL");

            return true;
        }

        if("SELL".equals(type)){
            //  买ycc  卖ycc换bty 卖bty


            client.postBill(amount3, symbol2,SBase, price2, "BUY");

//            if (amount2 > acc.get(symbol2).getActive()){
//                amount2 = acc.get(symbol2).getActive();
//            }
            client.postBill(amount2, symbol2,symbol1, price12, type);

//            if (amount1 > acc.get(symbol1).getActive()){
//                amount1 = acc.get(symbol1).getActive();
//            }
            client.postBill(amount1, symbol1,SBase, price1, "SELL");


            return true;
        }
        return  false;
    }

}
