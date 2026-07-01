package com.codemonks.api_gateway.util;

import com.codemonks.api_gateway.exception.ErrorResponse;
import com.codemonks.api_gateway.exception.GatewayException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ResponseParser {

    private final ObjectMapper objectMapper;

    public <T> Mono<T> parseResponse(String body, Class<T> responseType) {

        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            // Business Error Response
            if (jsonNode.has("responseCode")) {
                ErrorResponse errorResponse = objectMapper.treeToValue(jsonNode, ErrorResponse.class);
                return Mono.error(new GatewayException(errorResponse));
            }
            T response = objectMapper.treeToValue(jsonNode, responseType);
            return Mono.just(response);

        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}