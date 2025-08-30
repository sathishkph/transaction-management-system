package com.outseer.tms.exception;


public class DuplicateTransactionIdException extends RuntimeException {
    public DuplicateTransactionIdException( String message) {
        super(message);
    }
}
