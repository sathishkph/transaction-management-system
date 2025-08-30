package com.outseer.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {
    @NotBlank
    private String transactionId;
    @NotBlank
    private String userId;

    private Double amount;
    @NotBlank
    private String timeStamp;
}
