package com.outseer.tms.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AccountDto {
        private String userId;
        private Double currentBalance;
        private Integer transactionCount;
}
