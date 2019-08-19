package com.transaction.core.exchange.zt.job;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.exchange.ExStartConst;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.zt.ZTCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LoginJob implements ApplicationRunner {

    private final static String userName = "15957180382";
    private final static String password = "kfm14108116";
    private final static String uri="https://www.zt.com/api/v1/login";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(ExStartConst.ZTCNTSTART == 1 || ExStartConst.ZTSTART == 1){
            new Thread(()->{
                while (true){
                    try {
                        doLogin();
                    }catch (Exception e){
                        log.error("调用ZT登录接口发生异常",e);
                    }finally {
                        try {
                            TimeUnit.HOURS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void doLogin(){
        RestTemplate restTemplate = RestTemplateStatic.restTemplate();
        Map<String,String> map = new HashMap<>();
        map.put("username",userName);
        map.put("password",password);
        HttpEntity<Map> entity = new HttpEntity<>(map);
        String result = restTemplate.exchange(uri, HttpMethod.POST,entity,String.class).getBody();
        log.info("调用ZT登录接口返回结果：{}",result);
        JSONObject object = JSONObject.parseObject(result);
        if(!Integer.valueOf(0).equals(object.getInteger("code"))){
            log.error("调用ZT登录接口失败");
        }
        ZTCache.token = object.getJSONObject("result").getString("token");
    }

//    public static void main(String[] args) {
//        new LoginJob().doLogin();
//    }
}
