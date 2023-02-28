package com.wangjg.outbox.exception;

public class OutboxException extends RuntimeException{

    public OutboxException(String message) {
        super(message);
    }
}
