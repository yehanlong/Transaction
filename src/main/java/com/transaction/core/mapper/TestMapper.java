package com.transaction.core.mapper;

import com.transaction.core.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: testmapper
 * @author: yhl
 * @create: 2019-08-09
 */
@Mapper
public interface TestMapper {

    void addOrder(Order order) throws Exception;
}
