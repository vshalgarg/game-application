package com.codemonks.gameservice.feign;

import com.codemonks.gameservice.dto.external.ExternalServiceErrorResponse;
import com.codemonks.gameservice.exceptions.ExternalServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(
            String methodKey,
            Response response
    ) {
        try {
            String body =
                    Util.toString(
                            response.body().asReader()
                    );

            ExternalServiceErrorResponse error =
                    objectMapper.readValue(
                            body,
                            ExternalServiceErrorResponse.class
                    );

            Integer errorCode =
                    error.getErrorCode() != null
                            ? error.getErrorCode()
                            : response.status();

            String errorMessage =
                    error.getErrorMessage() != null
                            ? error.getErrorMessage()
                            : "External service error";

            return new ExternalServiceException(
                    errorCode,
                    errorMessage
            );

        } catch (Exception e) {
            return new ExternalServiceException(
                    response.status(),
                    "External service error"
            );
        }
    }
}