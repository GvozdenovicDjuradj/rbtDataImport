package com.rbt.test.error;

public class ResourceNotFoundException extends RbtException {
    public ResourceNotFoundException(){
        this("resource_not_found");
    }

    public ResourceNotFoundException(String message) {
        this(404, message);
    }

    public ResourceNotFoundException(String message, Exception cause) {
        this(404, message, cause);
    }

    public ResourceNotFoundException(int code, String message) {
        super(code, message);
    }

    public ResourceNotFoundException(int code, String message, Exception cause) {
        super(code, message, cause);
    }
}
