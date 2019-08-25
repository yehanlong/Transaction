package com.transaction.core.exchange.zt.job;

import com.alibaba.fastjson.JSONObject;
import com.transaction.core.dao.SMSDao;
import com.transaction.core.entity.SMS;
import com.transaction.core.exchange.ExStartConst;
import com.transaction.core.exchange.pub.RestTemplateStatic;
import com.transaction.core.exchange.zt.ZTCache;
import com.transaction.core.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

    private final static String sendSMGMail = "ZT登录需要手机验证码，已向手机号"+userName+"发送验证码短信，请将验证码输入到数据库的sms表中，程序将会自动读取进行登录认证";
    private final static String sendLoginSuccessMail = "ZT手机验证码登录成功";
    private final static String sendLoginFailMail = "ZT手机验证码登录失败";

    @Autowired
    private SMSDao smsDao;

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
        int needSafe = object.getJSONObject("result").getInteger("need_safe");
        if(1 == needSafe){
            log.info("ZT登录需要手机验证码");
            ZTCache.token = "Bearer "+object.getJSONObject("result").getString("token");
            boolean success = sendSMG();
            try {
                if(success){
                    ZTCache.token = "Bearer "+object.getJSONObject("result").getString("token");
                    MailUtil.sendEmains(sendLoginSuccessMail);
                }else{
                    MailUtil.sendEmains(sendLoginFailMail);
                }
            }catch (Exception e){
                log.error("发送登录成功邮件发生异常",e);
            }
        }else{
            ZTCache.token = "Bearer "+object.getJSONObject("result").getString("token");
        }
    }

    public boolean sendSMG(){
        String url = "https://www.zt.com/api/v1/user/SMS";
        RequestBody body = new FormBody.Builder()
                .add("type","0")
                .add("use_type","1")
                .add("phone","")
                .add("email","")
                .add("country_id","")
                .build();
        String responseData = post(url,body,ZTCache.token);
        JSONObject object = JSONObject.parseObject(responseData);

        if(object == null){
            log.error("ZT登录发送短信验证码失败，返回数据：{}",responseData);
            return false;
        }

        Integer code = object.getInteger("code");
        if(code == null || 0 != code){
            log.error("ZT登录发送短信验证码失败，返回数据：{}",responseData);
            return false;
        }
        log.info("发送短信验证码成功，开始邮件通知");
        try {
            MailUtil.sendEmains(sendSMGMail);
        }catch (Exception e){
            log.error("发送邮件失败",e);
        }
        return waitInputVerifyCode();
    }

    public boolean waitInputVerifyCode(){
        while (true){
            SMS sms = smsDao.getOne();
            if(sms == null){
                try {
                    Thread.sleep(10000);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            String code = sms.getCode();

            boolean result = safeLogin(code);
            smsDao.delete(sms);
            return result;
        }
    }

    public boolean safeLogin(String verifyCode){
        String url = "https://www.zt.com/api/v1/user/safeLogin";
        RequestBody body = new FormBody.Builder()
                .add("sms_code",verifyCode)
                .add("email_code","")
                .add("two_step_code","")
                .build();
        String responseData = post(url,body,ZTCache.token);
        JSONObject object = JSONObject.parseObject(responseData);

        if(object == null){
            log.error("ZT短信验证码登录失败，返回数据：{}",responseData);
            return false;
        }

        Integer code = object.getInteger("code");
        if(code == null || 0 != code){
            log.error("ZT短信验证码登录失败，返回数据：{}",responseData);
            return false;
        }
        return true;
    }

    public String post(String url,RequestBody body,String token) {

        OkHttpClient client = new OkHttpClient();
        try {
            log.info("post请求，url={}，param={}，token={}",url,body,token);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization",token)
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            int code = response.code();
            String responseData = response.body().string();
            log.info(responseData);
            if(code == 200){
                return responseData;
            }else{
                throw new RuntimeException("error http code is "+code);
            }
        }catch (Exception e){
            log.error("post请求出错，url={}，param={}，message={}",url,JSONObject.toJSONString(body),e.getMessage());
            throw new RuntimeException("post请求出错",e);
        }

    }

    public static void main(String[] args) {
        new LoginJob().doLogin();
        System.out.println(ZTCache.token);
    }
}
