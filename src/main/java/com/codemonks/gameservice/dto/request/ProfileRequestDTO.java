package com.codemonks.gameservice.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {

    private Long userId;

    private String name;

    private String email;
}