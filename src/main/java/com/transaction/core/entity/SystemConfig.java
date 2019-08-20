//package com.transaction.core.entity;
//
//import lombok.Data;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Index;
//import javax.persistence.Table;
//import java.math.BigDecimal;
//
//@Entity
//@Data
//@Table(indexes = {@Index(name = "idx_system_config_platform",columnList = "platform",unique = true)})
//public class SystemConfig {
//    @Column(nullable = false)
//    private Long id;
//    /**交易所名称*/
//    @Column(nullable = false,length = 20)
//    private String platform;
//    /**是否启动交易*/
//    @Column(nullable = false)
//    private Boolean enabled;
//    /**总的手续费*/
//    @Column(columnDefinition = "decimal(10,6) not null comment '总的手续费'")
//    private BigDecimal fee;
//    /**每次循环睡眠时间，单位毫秒*/
//    @Column(columnDefinition = "int not null default 1000 comment '每次循环睡眠时间，单位毫秒'")
//    private int loopSleepTime;
//    /**单笔交易最大交易金额*/
//    @Column(columnDefinition = "decimal(10,6) not null comment '单笔交易最大交易金额'")
//    private BigDecimal maxTradeAmount;
//    /**日志输出限制，当预计收益>(maxTradeAmount*logOutLimit)时才会输出日志，*/
//    @Column(columnDefinition = "decimal(10,6) not null default 1.00 comment '总的手续费'")
//    private BigDecimal logOutLimit;
//}
