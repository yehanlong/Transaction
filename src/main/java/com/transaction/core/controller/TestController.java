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

    @GetMapping(value = "account")
    public String  getAccount(){
        String uri="https://api.biqianbao.top/api/Account/Asset";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.add("FZM-REQUEST-OS", "FZM-REQUEST-OS");
        headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
        //headers.add("Authorization ", "Bearer e243dff6f8132ef254fae4e1f628e6d7966f8645");
        HttpEntity entity = new HttpEntity<>(headers);
        String result =restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
        return result;
    }
}
