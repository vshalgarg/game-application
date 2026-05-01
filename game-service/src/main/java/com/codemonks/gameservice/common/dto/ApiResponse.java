package com.codemonks.gameservice.common.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ApiError error;
    private Meta meta;


    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(Meta.now())
                .build();
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ApiError(code, message))
                .meta(Meta.now())
                .build();
    }
}
