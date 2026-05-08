package com.codemonks.gameservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServiceErrorResponse {

    private Integer errorCode;
    private String errorMessage;
}
