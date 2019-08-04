package com.transaction.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: test
 * @author: yhl
 * @create: 2019-08-04
 */
@RestController
@RequestMapping(value = "/api")
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "test")
    public void restTemplateTest(){

        Map map1=restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN",Map.class);
        System.out.println(map1.get("errmsg"));
    }
}
