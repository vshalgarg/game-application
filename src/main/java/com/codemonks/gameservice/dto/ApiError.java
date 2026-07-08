package com.codemonks.gameservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

    private int responseCode;
    private String message;
}
