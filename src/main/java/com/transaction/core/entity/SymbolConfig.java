package com.transaction.core.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(indexes = {@Index(name = "idx_symbol_platform",columnList = "platform,baseCoin,symbol1,symbol2",unique = true)})
public class SymbolConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar(20) not null comment '平台名称'")
    private String platform;
    @Column(nullable = false)
    private Boolean enabled;
    /**基础法币*/
    @Column(columnDefinition = "varchar(10) not null comment '基础法币'")
    private String baseCoin;
    @Column(columnDefinition = "varchar(10) not null")
    private String symbol1;
    @Column(columnDefinition = "varchar(10) not null")
    private String symbol2;
    /**要采用哪种策略交易*/
    @Column(columnDefinition = "varchar(10) not null comment '策略'")
    private String strategy;
    /**基础法币对symbol1的交易最小数量的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment '基础法币对symbol1的交易最小数量的小数位，整数为0'")
    private Integer baseSymbol1Amount;
    /**基础法币对symbol2的交易最小数量的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment '基础法币对symbol2的交易最小数量的小数位，整数为0'")
    private Integer baseSymbol2Amount;
    /**symbol1对symbol2的交易最小数量的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment 'symbol1对symbol2的交易最小数量的小数位，整数为0'")
    private Integer symbol1Symbol2Amount;
    /**symbol1对symbol2的交易最小价格的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment 'symbol1对symbol2的交易最小价格的小数位，整数为0'")
    private Integer baseSymbol1Price;
    /**symbol1对symbol2的交易最小价格的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment 'symbol1对symbol2的交易最小价格的小数位，整数为0'")
    private Integer baseSymbol2Price;
    /**symbol1对symbol2的交易最小价格的小数位，整数为0*/
    @Column(columnDefinition = "int not null comment 'symbol1对symbol2的交易最小价格的小数位，整数为0'")
    private Integer symbol1Symbol2Price;
}
