package com.outseer.tms.exception;

public class InsufficientBalanceException extends  RuntimeException {
    public InsufficientBalanceException(String message){
        super(message);
    }
}
