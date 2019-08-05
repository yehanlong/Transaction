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

    private String total;

    private String realactive;

    private String valuation;

    private String poundage;

    private String active;

    private String frozen;
}
