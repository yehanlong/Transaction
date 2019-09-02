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
 * @date 2019/9/2 12:17
 */
@Data
@Entity
@Table(name = "`order`")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer accountId;
    private String type;
    private String sy;
    private Double price;
    private Double amount;
    private Integer count;
    @CreatedDate
    private Date createTime;
}
