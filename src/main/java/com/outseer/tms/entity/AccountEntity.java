package com.outseer.tms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "Account")
@Data
@Entity
public class AccountEntity {
    @Id
    private String userId;
    private Double currentBalance;
    private Integer transactionCount;
}
