package com.outseer.tms.dto;

public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "Insufficient balance"),
    USER_ID_EXISTS("USER_ID_EXISTS", "User id exists");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

