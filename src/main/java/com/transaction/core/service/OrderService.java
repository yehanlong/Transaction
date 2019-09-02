package com.transaction.core.service;

import com.transaction.core.dao.OrderDao;
import com.transaction.core.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:23
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public OrderEntity save(OrderEntity orderEntity){
        return orderDao.save(orderEntity);
    }
}
