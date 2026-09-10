package com.codemonks.gameservice.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {

    private Long userId;

    private String name;

    private String email;

    private LocalDate dob;
}