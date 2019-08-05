package com.transaction.core.service;

/**
 * @description: 挂单
 * @author: yhl
 * @create: 2019-08-05
 */
public interface PostBillService {

    String postBill(String amount,String currency,String currency2,String price,String ty);
}
