package com.sparta.common.exception;

public class CustomException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;
    private final String message;  // 상태 코드 필드 추가

    public CustomException(int statusCode, String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

