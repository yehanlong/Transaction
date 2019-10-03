package com.transaction.core.strategy;

import com.transaction.core.entity.AmountPrice;
import com.transaction.core.entity.Order;
import com.transaction.core.entity.SyncMarkInfo;
import com.transaction.core.entity.vo.TradeVO;
import com.transaction.core.exchange.pub.MarketInfo;
import com.transaction.core.exchange.pub.PubConst;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.pubinterface.Exchange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ROUND_HALF_DOWN;


// 用于模拟计算一轮交易后的金额

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirstCacl {


    private Exchange client;

    private AmountPrice amountPrice;

    public FirstCacl(Exchange client){
        this.client = client;
    }

    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public double getFirstCount(String sy1, String sy2, String sBase, String type){


        if (type == "BUY"){
            SyncMarkInfo syncMarkInfo = MarketInfo.syncGetMarketInfo(client,sy1,sy2,sBase);
//            SyncMarkInfo syncMarkInfo = client.getSyncMarkInfo(sy1, sy2, sBase);
            if (syncMarkInfo.getTrade1().getSuccess() && syncMarkInfo.getTrade2().getSuccess() && syncMarkInfo.getTrade3().getSuccess()) {
                // 都获取成功
            } else {
                return 0.0;
            }


            // 获取bty价格
            TradeVO BTYMarket = syncMarkInfo.getTrade1();
            // 获取bty-ycc价格
            TradeVO BTYYCCMarket = syncMarkInfo.getTrade2();
            // 获取ycc价格
            TradeVO YCCMarket = syncMarkInfo.getTrade3();

            Order btyO = Deal.dealSmallOrder(BTYMarket.getSells());
            double btyPrice = btyO.getPrice();
            double btyNum = btyO.getAm();

            Order ybO = Deal.dealSmallOrder(BTYYCCMarket.getSells());
            double ybPrice = ybO.getPrice();
            double ybNum = ybO.getAm();

            Order yccO = Deal.dealSmallOrder(YCCMarket.getBuys());
            double yccPrice = yccO.getPrice();
            double yccNum = yccO.getAm();


            AmountPrice amountPrice = new AmountPrice();
            amountPrice.setSy1Amount(btyNum);
            amountPrice.setSy1Price(btyPrice);

            amountPrice.setSy12Amount(ybNum);
            amountPrice.setSy12Price(ybPrice);

            amountPrice.setSy2Amount(yccNum);
            amountPrice.setSy2Price(yccPrice);

            BigDecimal usdtcountB = this.getUSDTcount(amountPrice,"BUY");

//            logger.info(amountPrice.toString());

            this.amountPrice = amountPrice;

            return usdtcountB.doubleValue();
        }

        if (type == "SELL"){
//            SyncMarkInfo syncMarkInfo = client.getSyncMarkInfo(sy1, sy2, sBase);
            SyncMarkInfo syncMarkInfo = MarketInfo.syncGetMarketInfo(client,sy1,sy2,sBase);

            if (syncMarkInfo.getTrade1().getSuccess() && syncMarkInfo.getTrade2().getSuccess() && syncMarkInfo.getTrade3().getSuccess()) {
                // 都获取成功
            } else {
               return 0.0;
            }


            // 获取bty价格
            TradeVO BTYMarket = syncMarkInfo.getTrade1();
            // 获取bty-ycc价格
            TradeVO BTYYCCMarket = syncMarkInfo.getTrade2();
            // 获取ycc价格
            TradeVO YCCMarket = syncMarkInfo.getTrade3();

            Order yccO = Deal.dealSmallOrder(YCCMarket.getSells());
            double yccPrice = yccO.getPrice();
            double yccNum = yccO.getAm();

            Order ybO = Deal.dealSmallOrder(BTYYCCMarket.getBuys());
            double ybPrice = ybO.getPrice();
            double ybNum = ybO.getAm();

            Order btyO = Deal.dealSmallOrder(BTYMarket.getBuys());
            double btyPrice = btyO.getPrice();
            double btyNum = btyO.getAm();

            AmountPrice amountPrice = new AmountPrice();
            amountPrice.setSy1Amount(btyNum);
            amountPrice.setSy1Price(btyPrice);

            amountPrice.setSy12Amount(ybNum);
            amountPrice.setSy12Price(ybPrice);

            amountPrice.setSy2Amount(yccNum);
            amountPrice.setSy2Price(yccPrice);

            BigDecimal usdtcountB = this.getUSDTcount(amountPrice,"SELL");

            this.amountPrice = amountPrice;

            return usdtcountB.doubleValue();
        }


        return 0.0;


    }


    // 模拟技计算一轮后的usdt是多少  type第二步是买还是卖
    // 测试过，结果正确
    private BigDecimal getUSDTcount(AmountPrice amountPrice, String type){
        double btyPrice = amountPrice.getSy1Price();
        double btyNum = amountPrice.getSy1Amount();

        double ybPrice = amountPrice.getSy12Price();
        double ybNum = amountPrice.getSy12Amount();

        double yccPrice = amountPrice.getSy2Price();
        double yccNum = amountPrice.getSy2Amount();

        double usdt = 5.0;
        double minUsdt = PubConst.minUSDT;

        if (type == "SELL"){
            // 第一步
            // usdt 买ycc
            BigDecimal yccPriceB = new BigDecimal(yccPrice);
            BigDecimal yccNumB = new BigDecimal(yccNum);
            int a = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(minUsdt));
            if (a == -1) {
                // 当获取失败或者金额太少，就放弃此次循环
                return new BigDecimal(1.0);
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
                return new BigDecimal(1.0);
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
                return new BigDecimal(1.0);
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
                return new BigDecimal(1.0);
            }
            BigDecimal btyCountB = ((new BigDecimal(usdt)).divide(btyPriceB,25,ROUND_HALF_DOWN));
            //double btyCount = usdt/btyPrice;

            // bty 买ycc

            BigDecimal ybPriceB =new BigDecimal(ybPrice);
            BigDecimal ybNumB = new BigDecimal(ybNum);
            int a = (ybPriceB.multiply(ybNumB)).multiply(btyPriceB).compareTo(new BigDecimal(minUsdt));
            if (a == -1) {
                return new BigDecimal(1.0);
            }
            BigDecimal yccCountB = btyCountB.divide(ybPriceB,25,ROUND_HALF_DOWN);
            //double yccCount = btyCount/ybPrice;

            // 卖掉ycc

            BigDecimal yccPriceB =new BigDecimal(yccPrice);
            BigDecimal yccNumB = new BigDecimal(yccNum);
            BigDecimal usdtCountB = yccCountB.multiply(yccPriceB);
            int a1 = (yccPriceB.multiply(yccNumB)).compareTo(new BigDecimal(minUsdt));
            if(a1 == -1){
                return new BigDecimal(1.0);
            }
            return usdtCountB;
        }

        return new BigDecimal(0.0);

    }

}
