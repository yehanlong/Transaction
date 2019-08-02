package com.transaction.core;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.response.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Autowired
    private RestTemplate restTemplate;

    public Object restTemplateDemo(){

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
