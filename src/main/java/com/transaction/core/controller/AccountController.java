package com.transaction.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.core.entity.PropertyEntity;
import com.transaction.core.response.ResponseBean;
import com.transaction.core.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @description: test
 * @author: yhl
 * @create: 2019-08-04
 */
@RestController
@RequestMapping(value = "/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "/account/{name}")
    public ResponseBean getAccountByName(@PathVariable(value = "name")String name) throws IOException {
        try{
            Map<String,PropertyEntity> map= accountService.getAccount();
            PropertyEntity propertyEntity = map.get(name);
            return ResponseBean.ok("获取"+name+"信息成功",propertyEntity);
        }catch (Exception e){
            return ResponseBean.fail("获取"+name+"信息失败"+e.getMessage());
        }
    }

    @GetMapping(value = "account")
    public ResponseBean getAllAccount() throws IOException {
        try{
           return new  ResponseBean<>(ResponseBean.SUCCESS,"获取账户信息成功",accountService.getAccount());
        }catch (Exception e){
            return ResponseBean.fail("获取账户信息失败"+e.getMessage());
        }
    }

}
