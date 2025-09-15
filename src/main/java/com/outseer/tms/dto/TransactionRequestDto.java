package com.outseer.tms.dto;

import com.outseer.tms.helper.IsoTimestamp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {
    @NotBlank
    private String transactionId;
    @NotBlank
    private String userId;

    @NotNull
    private BigDecimal amount;
    @NotBlank
    @IsoTimestamp
    private String timeStamp;
}
