package com.codemonks.api_gateway.auth.dto.request;


import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    private Long userId;

    private String name;

    private String email;

    private String dob;
}