package com.outseer.tms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table(name = "transactions", indexes = {
        @Index(name = "idx_user_id", columnList = "userId")
})
@Entity
public class TransactionEntity {
    @Id
    private String transactionId;
    private String userId;
    @Column(precision = 19, scale = 4)
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
