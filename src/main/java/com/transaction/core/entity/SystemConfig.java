package com.transaction.core.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(indexes = {@Index(name = "idx_system_platform",columnList = "platform",unique = true)})
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**交易所名称*/
    @Column(nullable = false,length = 20)
    private String platform;
    /**是否启动交易所*/
    @Column(nullable = false)
    private Boolean enabled;

    /**每次循环睡眠时间，单位毫秒*/
    @Column(columnDefinition = "int not null default 1000 comment '每次循环睡眠时间，单位毫秒'")
    private int loopSleepTime;
    /**日志输出限制，当预计收益>logOutLimit时才会输出日志，*/
    @Column(columnDefinition = "decimal(10,6) not null default 5.01 comment '总的手续费'")
    private BigDecimal logOutLimit;

    /**盈利百分比，预计盈利大于这个值才会开始交易*/
    @Column(columnDefinition = "decimal(10,6) not null default 0.008 comment '盈利百分比'")
    private BigDecimal startPercentage;

    /**单笔交易手续费*/
    @Column(columnDefinition = "decimal(10,6) not null default 0.001 comment '单笔交易手续费'")
    private BigDecimal fee;

    private Date cacheTime;
}
