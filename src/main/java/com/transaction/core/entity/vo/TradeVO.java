package com.transaction.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private String price;
    private String am;
    private String time;
    private String type;
}
