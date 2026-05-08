package com.codemonks.gameservice.exceptions;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{

    private final ResponseErrorCodes errorCode;

    public ResourceNotFoundException(ResponseErrorCodes errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
