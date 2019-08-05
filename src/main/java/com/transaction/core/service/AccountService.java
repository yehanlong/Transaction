package com.transaction.core.service;

import com.transaction.core.entity.PropertyEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @description: 账户service
 * @author: yhl
 * @create: 2019-08-05
 */
public interface AccountService {

    Map getAccount() throws IOException;
}
