package com.codemonks.gameservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {

    private Long userId;

    private String name;

    private String email;

    private LocalDate dob;

    private Boolean profileStatus;
}