package com.codemonks.gameservice.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RemovePlayerRequestDTO {

    @NotNull
    private Long hostUserId;

    @NotNull
    private Long userId;
}

