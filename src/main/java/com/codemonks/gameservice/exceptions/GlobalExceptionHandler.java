package com.codemonks.gameservice.exceptions;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {

        log.error("Unhandled exception occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        500,
                        INTERNAL_SERVER_ERROR.toString()
                ));
    }

    @ExceptionHandler(GameException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameException(GameException ex) {

        ResponseErrorCodes error = ex.getErrorCode();
        return ResponseEntity
                .ok()
                .body(ApiResponse.failure(
                        error.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex){

        ResponseErrorCodes errorCode = ex.getErrorCode();
        return ResponseEntity
                .ok()
                .body(ApiResponse.failure(
                        errorCode.getCode(),
                        errorCode.getMessage()
                ));
    }

    @ExceptionHandler(
            ExternalServiceException.class
    )
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(ExternalServiceException ex
    ) {

        log.error(
                "External service exception occurred",
                ex
        );
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        ApiResponse.failure(
                                ex.getExternalStatusCode(),
                                ex.getMessage()
                        )
                );
    }
}
