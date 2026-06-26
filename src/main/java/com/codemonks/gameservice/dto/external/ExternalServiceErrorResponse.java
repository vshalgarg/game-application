package com.codemonks.gameservice.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServiceErrorResponse {

    @JsonProperty("errorCode")
    private Integer errorCode;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    public String getErrorMessage() {
        return errorMessage != null ? errorMessage : message;
    }
}
