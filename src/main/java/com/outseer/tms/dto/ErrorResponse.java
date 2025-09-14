package com.outseer.tms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {
    @NonNull
    private ErrorCode errorCode;
    @NonNull
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude if null
    private Map<String, String> errors;
}
