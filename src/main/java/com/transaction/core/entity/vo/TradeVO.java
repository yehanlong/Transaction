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
    private Boolean success;
    private Long time;

    public List<Order> getBuys() {
        return buys;
    }

    public void setBuys(List<Order> buys) {
        this.buys = buys;
    }

    public List<Order> getSells() {
        return sells;
    }

    public void setSells(List<Order> sells) {
        this.sells = sells;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public TradeVO(List<Order> buys, List<Order> sells) {
        this.success = true;
        this.buys = buys;
        this.sells = sells;
    }
}
