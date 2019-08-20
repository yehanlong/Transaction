package com.transaction.core.exchange.pub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.vo.PropertyVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description: resttemplate
 * @author: yhl
 * @create: 2019-08-05
 */
public class RestTemplateStatic {

    public static RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(10000);//单位为ms
        factory.setConnectTimeout(10000);//单位为ms
        return new RestTemplate(factory);
    }


    /*
    public static void main(String[] args) {
    RestTemplate restTemplate = RestTemplateStatic.restTemplate();
    String uri="https://api.biqianbao.top/api/Account/Asset";
    HttpHeaders headers = new HttpHeaders();
    //定义请求参数类型
    headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
    headers.add("FZM-REQUEST-OS", "FZM-REQUEST-OS");
    headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
    //headers.add("Authorization ", "Bearer e243dff6f8132ef254fae4e1f628e6d7966f8645");
    HttpEntity entity = new HttpEntity<>(headers);
    String result =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class).getBody();
    JSONObject object = JSON.parseObject(result);
    JSONObject jsonData = object.getJSONObject("data");
    String valuation = jsonData.getString("valuation");
    System.out.println("资产总数为："+valuation);
    JSONObject jsonList = jsonData.getJSONObject("list");
    Map<String, PropertyVO> map = new HashMap<>();
    Set<String> keySet = jsonList.keySet();
    for(String key:keySet){
        // 获得key
        JSONObject jsonBTY = jsonList.getJSONObject(key);
        PropertyVO propertyVO = JSON.parseObject(jsonBTY.toJSONString(), PropertyVO.class);
        map.put(key, propertyVO);
    }
    return ;
}*/

}
