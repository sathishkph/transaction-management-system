package com.outseer.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private String userId;
    private BigDecimal currentBalance;
    private Long transactionCount;
}
