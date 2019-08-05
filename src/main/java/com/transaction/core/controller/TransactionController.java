package com.transaction.core.controller;

import com.transaction.core.service.PostBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 挂单
 * @author: yhl
 * @create: 2019-08-05
 */
@RestController
@RequestMapping(value = "api")
public class TransactionController {

    @Autowired
    private PostBillService postBillService;

    @GetMapping(value = "test")
    public String postBill(@RequestParam String amount, String currency, String currency2, String price, String ty){
        return postBillService.postBill(amount,currency,currency2,price,ty);
    }
}
