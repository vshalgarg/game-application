package com.codemonks.gameservice.feign;

import com.codemonks.gameservice.dto.external.ExternalServiceErrorResponse;
import com.codemonks.gameservice.exceptions.ExternalServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codemonks.gameservice.constants.ResponseErrorCodes.EXTERNAL_SERVICE_ERROR;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(
            String methodKey,
            Response response
    ) {
        try {String body = Util.toString(response.body().asReader());

            log.error("External service call failed");
            log.error(
                    "Feign error occurred. MethodKey: {}, Status: {}, ResponseBody: {}",
                    methodKey,
                    response.status(),
                    body
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
                            : String.valueOf(EXTERNAL_SERVICE_ERROR);

            return new ExternalServiceException(
                    errorCode,
                    errorMessage
            );

        } catch (Exception e) {
            log.error(
                    "Failed to decode feign error response. MethodKey: {}, Status: {}",
                    methodKey,
                    response.status(),
                    e
            );
            return new ExternalServiceException(
                    response.status(),
                    String.valueOf(EXTERNAL_SERVICE_ERROR)
            );
        }
    }
}