package com.transaction.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 账户币种信息
 * @author: yhl
 * @create: 2019-08-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PropertyVO {

    private String name;

    private Double total;

    private Double realactive;

    private Double valuation;

    private Double poundage;

    private Double active;

    private Double frozen;
}
