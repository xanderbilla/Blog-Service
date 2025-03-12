package com.kallan.clan.exception;

import lombok.Getter;

@Getter
public class AIServiceException extends RuntimeException {
    private final String errorCode;

    public AIServiceException(String message) {
        super(message);
        this.errorCode = "AI_SERVICE_ERROR";
    }

    public AIServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
