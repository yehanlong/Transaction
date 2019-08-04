package com.transaction.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Account {
    /**币种**/
    private String currency;
    /**可用余额**/
    private double active;
    /**冻结余额**/
    private double frozen;
}
