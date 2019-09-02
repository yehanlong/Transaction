package com.transaction.core.dao;

import com.transaction.core.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:21
 */
public interface OrderDao extends JpaRepository<OrderEntity,Integer> {

}
