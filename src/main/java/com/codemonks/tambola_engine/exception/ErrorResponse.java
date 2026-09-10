package com.codemonks.tambola_engine.exception;

import lombok.Builder;
import lombok.Data;

// Ludo ke ErrorResponse jaisa hi generic shape - saare exceptions isi
// ek consistent structure me client ko response bhejte hain.
@Data
@Builder
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
}