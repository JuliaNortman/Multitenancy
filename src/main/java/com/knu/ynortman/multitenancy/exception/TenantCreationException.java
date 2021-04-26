package com.knu.ynortman.multitenancy.exception;

public class TenantCreationException extends Exception{
    public TenantCreationException() {
        super();
    }
    public TenantCreationException(String message) {
        super(message);
    }

    public TenantCreationException(String message, Exception e) {
        super(message,e);
    }
}
