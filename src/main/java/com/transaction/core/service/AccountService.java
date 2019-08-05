package com.transaction.core.service;

import com.transaction.core.entity.vo.TradeVO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description: 账户service
 * @author: yhl
 * @create: 2019-08-05
 */
public interface AccountService {

    Map getAccount() throws IOException;

    List<TradeVO> getZBMarketInfo(int num, String symbol) throws Exception;
}
