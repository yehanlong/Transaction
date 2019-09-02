package com.transaction.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:13
 */
@Data
@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sy1;
    @Column(name = "sy1_amount")
    private Double sy1Amount;
    private String sy2;
    @Column(name = "sy2_amount")
    private Double sy2Amount;
    private String sbase;
    private Double sbaseAmount;
    private Double cacl;
    private Integer count;
    @CreatedDate
    private Date createTime;
}
