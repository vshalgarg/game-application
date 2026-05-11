package com.codemonks.gameservice.exceptions;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final Integer externalStatusCode;

    public ExternalServiceException(
            Integer errorCode,
            String message
    ) {
        super(message);
        this.externalStatusCode = errorCode != null ? errorCode : 502;
    }
}
