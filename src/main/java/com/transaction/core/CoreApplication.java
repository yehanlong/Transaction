package com.transaction.core;

import com.transaction.core.exchange.zhaobi.Moving1;
import com.transaction.core.exchange.zhaobi.Moving2;
import com.transaction.core.exchange.zhaobi.ZhaobiClient;
import com.transaction.core.exchange.zhaobi.ZhaobiInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);

        ZhaobiInit zbi = new ZhaobiInit();
        ZhaobiClient ZBClient = new ZhaobiClient();
        Lock lock = new ReentrantLock();
        //启动线程
        Map<String,String> syMap1 = zbi.initSymbol1();
        for (Map.Entry<String, String> entry : syMap1.entrySet()) {
            Moving1 m1 = new Moving1(ZBClient,entry.getKey(),entry.getKey());
            m1.setLock(lock);
            m1.start();
        }

        Map<String,String> syMap2 = zbi.initSymbol2();
        for (Map.Entry<String, String> entry : syMap2.entrySet()) {
            Moving2 m2 = new Moving2(ZBClient,entry.getKey(),entry.getKey());
            m2.setLock(lock);
            m2.start();
        }

//;
    }

    @Autowired
    private RestTemplate restTemplate;

    public Object restTemplateDemo(){

        Map map1=restTemplate.getForObject("https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=ACCESS_TOKEN",Map.class);
        System.out.println(map1.get("errmsg"));
        String uri="http://wthrcdn.etouch.cn/weather_mini?city=";
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.TEXT_PLAIN);
        //headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
        headers.add("User-Agent", "curl/7.58.0");
        //headers.add("Authorization ", "Basic YWRtaW46YWRxxxx=");
        //RestTemplate带参传的时候要用HttpEntity<?>对象传递
        //json参数（map）
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", 1);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(map, headers);
        ResponseEntity<Map> result =restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
        return result;
    }

}
