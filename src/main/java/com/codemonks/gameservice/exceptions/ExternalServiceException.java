package com.codemonks.gameservice.exceptions;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final Integer errorCode;

    public ExternalServiceException(
            Integer errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : 500;
    }
}
