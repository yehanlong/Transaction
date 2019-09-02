package com.transaction.core;

import com.transaction.core.entity.AccountEntity;
import com.transaction.core.entity.OrderEntity;
import com.transaction.core.service.AccountService;
import com.transaction.core.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreApplicationTests {

    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderService orderService;

    @Test
    public void testSaveAccount(){
        AccountEntity entity = AccountEntity.builder().sbase("YCCBTYUSDT").build();
        accountService.save(entity);
    }

    @Test
    public void testSaveOrder(){
        OrderEntity orderEntity = OrderEntity.builder().accountId(123).build();
        orderService.save(orderEntity);
    }

}
