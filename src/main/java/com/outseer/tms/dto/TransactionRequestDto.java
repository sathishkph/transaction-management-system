package com.outseer.tms.dto;

import com.outseer.tms.helper.IsoTimestamp;
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
    @IsoTimestamp
    private String timeStamp;
}
