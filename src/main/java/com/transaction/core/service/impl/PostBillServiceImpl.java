package com.transaction.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.transaction.core.entity.vo.PropertyVO;
import com.transaction.core.service.PostBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description: 挂单
 * @author: yhl
 * @create: 2019-08-05
 */
@Service
public class PostBillServiceImpl implements PostBillService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String postBill(String amount, String currency, String currency2, String price, String ty) {
        String uri="https://api.biqianbao.top/api/trade/place";
        String requestText = "amount=" + amount + "&" + "currency=" + currency + "&" + "currency2=" + currency2 + "&" + "price=" + price + "&" + "ty=" + ty;
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.setBearerAuth("e243dff6f8132ef254fae4e1f628e6d7966f8645");
        HttpEntity entity = new HttpEntity<>(requestText,headers);
        String result =restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
        JSONObject object = JSON.parseObject(result);
        return object.toJSONString();
    }
}
