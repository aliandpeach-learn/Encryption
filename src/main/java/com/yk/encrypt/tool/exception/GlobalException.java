package com.yk.encrypt.tool.exception;

public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 9149556066494930261L;

    private long id;

    private String message;

    public GlobalException(long id, String message) {
        this.id = id;
        this.message = message;
    }
}
