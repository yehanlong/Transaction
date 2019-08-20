package com.transaction.core.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "sms")
public class SMS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
}
