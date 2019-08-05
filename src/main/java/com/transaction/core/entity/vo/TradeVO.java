package com.transaction.core.entity.vo;

import com.transaction.core.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @description: 市场行情返回类
 * @author: yhl
 * @create: 2019-08-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TradeVO {

    private List<Order> buys;
    private List<Order> sells;
    private boolean success;
}
